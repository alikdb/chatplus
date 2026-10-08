/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.heads

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.ObjectContents
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.network.chat.contents.objects.PlayerSprite
import net.minecraft.util.StringDecomposer

/**
 * Inserts a head component into a chat message.
 *
 * The component tree is flattened into a list of sibling-less components (render order), literals
 * are searched for names, and the literal holding the name is split so the head lands right before it.
 *
 * Assumptions (from Chat Heads):
 *  - names only appear in literals and in the arguments of translatables
 *  - a name may span several consecutive literals (`<Pla><yer>`), but never a literal and a translatable,
 *    nor two translatable arguments
 *  - a message that already contains a player sprite has its head already
 *  - only one head is added per message
 */
object ComponentProcessor {
	class LiteralSequence(val text: String, val startIndex: Int, val endIndex: Int)

	private fun walkTree(component: Component, parentStyle: Style, consumer: (Component, Style) -> Unit) {
		val style = component.style.applyTo(parentStyle)
		consumer(component, style)
		for (sibling in component.siblings) walkTree(sibling, style, consumer)
	}

	/** Flattens the tree; legacy § codes inside literals are turned into styles. `siblings` of the result are mutable. */
	fun split(component: Component): ArrayList<Component> {
		val components = ArrayList<Component>()

		walkTree(component, Style.EMPTY) { c, cStyle ->
			val contents = c.contents
			if (contents is PlainTextContents) {
				var previousStyle = cStyle
				val builder = StringBuilder()

				StringDecomposer.iterateFormatted(contents.text(), cStyle) { _, style, codePoint ->
					if (style != previousStyle) {
						if (builder.isNotEmpty()) components.add(Component.literal(builder.toString()).setStyle(previousStyle))
						builder.setLength(0)
						previousStyle = style
					}
					builder.appendCodePoint(codePoint)
					true
				}

				if (builder.isNotEmpty()) components.add(Component.literal(builder.toString()).setStyle(previousStyle))
			} else {
				components.add(c.plainCopy().setStyle(cStyle))
			}
		}

		return components
	}

	/** `join(split(c))` renders the same as `c`. */
	fun join(components: List<Component>): Component {
		if (components.size == 1) return components[0]

		val combined = Component.empty()
		combined.siblings.addAll(components)
		return combined
	}

	fun containsPlayerSprite(components: List<Component>): Boolean =
		components.any { (it.contents as? ObjectContents)?.contents() is PlayerSprite }

	fun prependHead(message: Component, head: HeadSource): Component =
		Component.empty().append(head.createComponent(message)).append(message)

	/**
	 * Calls [onLiterals] for every run of consecutive literals and [onTranslatable] for every translatable,
	 * stopping as soon as one of them returns true.
	 */
	private fun walkLiteralsAndTranslatables(
		components: List<Component>,
		onLiterals: (LiteralSequence) -> Boolean,
		onTranslatable: (index: Int, contents: TranslatableContents) -> Boolean,
	) {
		val text = StringBuilder()
		var start = -1

		for ((i, component) in components.withIndex()) {
			val contents = component.contents
			if (contents is PlainTextContents) {
				if (start == -1) start = i
				text.append(contents.text())
				continue
			}

			if (text.isNotEmpty() && onLiterals(LiteralSequence(text.toString(), start, i - 1))) return
			text.setLength(0)
			start = -1

			if (contents is TranslatableContents && onTranslatable(i, contents)) return
		}

		if (text.isNotEmpty()) onLiterals(LiteralSequence(text.toString(), start, components.size - 1))
	}

	/** Puts [head] at [codePointIndex] of [sequence]. */
	private fun insertHead(components: ArrayList<Component>, sequence: LiteralSequence, codePointIndex: Int, head: HeadSource): Boolean {
		var remaining = codePointIndex

		for (i in sequence.startIndex..sequence.endIndex) {
			val literal = components[i]
			val text = (literal.contents as PlainTextContents).text()
			val length = text.codePointCount(0, text.length)

			if (remaining >= length) {
				remaining -= length
				continue
			}

			val headComponent = head.createComponent(literal)
			components[i] = if (remaining == 0) {
				Component.empty().append(headComponent).append(literal)
			} else {
				val cut = text.offsetByCodePoints(0, remaining)
				val left = Component.literal(text.substring(0, cut)).setStyle(literal.style)
				val right = Component.literal(text.substring(cut)).setStyle(literal.style)
				Component.empty().append(left).append(headComponent).append(right)
			}
			return true
		}

		return false
	}

	/** Runs [process] on the split arguments of a translatable and rebuilds it if something was found. */
	private fun <T : Any> processTranslatableArguments(
		translatable: Component,
		contents: TranslatableContents,
		process: (ArrayList<Component>) -> T?,
		onProcessed: (Component) -> Unit,
	): T? {
		val args = contents.args
		// the default "<%s> %s" template only has the sender name in its first argument
		val argsToCheck = if (contents.key == "chat.type.text") minOf(1, args.size) else args.size

		for (i in 0 until argsToCheck) {
			val arg = args[i]
			val argComponent = when (arg) {
				is Component -> arg
				is String -> Component.literal(arg)
				else -> continue
			}

			val splitArg = split(argComponent)
			val result = process(splitArg) ?: continue

			val newArgs = args.copyOf()
			newArgs[i] = join(splitArg)

			val rebuilt: MutableComponent = Component.translatableWithFallback(contents.key, contents.fallback, *newArgs)
			rebuilt.setStyle(translatable.style)
			onProcessed(rebuilt)
			return result
		}

		return null
	}

	/** Messages with a "suggest /tell <name>" click event, e.g. vanilla chat names. */
	fun addHeadForClickTell(components: ArrayList<Component>, lookup: NameLookup): HeadSource? {
		for (i in components.indices) {
			val component = components[i]

			val receiver = tellReceiver(component)
			if (receiver != null) {
				val info = lookup[receiver]
				if (info != null) {
					val head = HeadSource.of(info)
					components[i] = Component.empty().append(head.createComponent(component)).append(component)
					return head
				}
			}

			val contents = component.contents
			if (contents is TranslatableContents) {
				val head = processTranslatableArguments(component, contents, { addHeadForClickTell(it, lookup) }) { components[i] = it }
				if (head != null) return head
			}
		}

		return null
	}

	private fun tellReceiver(component: Component): String? {
		val event = component.style.clickEvent as? ClickEvent.SuggestCommand ?: return null
		val command: String? = event.command()
		return if (command != null && command.startsWith("/tell ")) command.substring("/tell ".length).trim() else null
	}

	/** Finds the first known player name and puts the head before it (Chat Heads' heuristic). */
	fun addHeadForPlayerName(components: ArrayList<Component>, lookup: NameLookup): HeadSource? {
		var found: HeadSource? = null

		walkLiteralsAndTranslatables(components, { sequence ->
			val match = NameScanner.scan(sequence.text, lookup)
			if (match != null && insertHead(components, sequence, match.second, HeadSource.of(match.first))) {
				found = HeadSource.of(match.first)
				true
			} else {
				false
			}
		}) { index, contents ->
			found = processTranslatableArguments(components[index], contents, { addHeadForPlayerName(it, lookup) }) { components[index] = it }
			found != null
		}

		return found
	}

	/**
	 * Lets [locate] pick a position inside the text the message starts with, e.g. the Hypixel sender parser.
	 * [locate] returns the head and the UTF-16 index inside the given text, or null.
	 */
	fun addHeadAtStart(components: ArrayList<Component>, locate: (String) -> Pair<HeadSource, Int>?): HeadSource? {
		var found: HeadSource? = null

		walkLiteralsAndTranslatables(components, { sequence ->
			if (sequence.startIndex == 0) {
				locate(sequence.text)?.let { (head, index) ->
					if (insertHead(components, sequence, sequence.text.codePointCount(0, index), head)) found = head
				}
			}
			true // only the leading text is a sender prefix
		}) { _, _ -> true }

		return found
	}
}
