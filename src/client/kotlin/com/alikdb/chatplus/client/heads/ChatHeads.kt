/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.heads

import com.alikdb.chatplus.client.config.ChatPlusConfig
import com.alikdb.chatplus.client.config.HeadPosition
import com.alikdb.chatplus.client.config.SenderDetection
import com.alikdb.chatplus.client.hypixel.Hypixel
import com.alikdb.chatplus.client.hypixel.HypixelSenderParser
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.multiplayer.chat.GuiMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.contents.TranslatableContents
import kotlin.math.roundToInt

/*
 * Call paths into handleAddedMessage (all on the client thread):
 *
 * ClientPacketListener.handlePlayerChat()    -> ChatListener.showMessageToPlayer()        -> ChatComponent.addPlayerMessage()
 * ClientPacketListener.handleDisguisedChat() -> ChatListener.handleDisguisedChatMessage() -> ChatComponent.addPlayerMessage()
 * ClientPacketListener.handleSystemChat()    -> ChatListener.handleSystemMessage()        -> ChatComponent.addServerSystemMessage()
 *
 * Hypixel sends everything, player chat included, as system messages.
 */
object ChatHeads {
	private val config get() = ChatPlusConfig.get().chatHeads

	/** Sender of the message currently being added; used by "before line" rendering. */
	@JvmStatic
	var lastSender: HeadSource? = null

	/** Set once the server tells us who sent a message, which makes guessing unnecessary. */
	@Volatile
	private var serverSentUuid = false

	// "before line" rendering state, captured during ChatComponent rendering
	@JvmStatic
	var chatGraphicsAccess: ChatComponent.ChatGraphicsAccess? = null

	/** True while text with heads is laid out or drawn in the chat (padding, shadow and 3D only apply there). */
	@JvmStatic
	var customHeadRendering = false

	/** Inside ChatComponent.logChatMessage: heads are dropped from the log instead of showing "[Name head]". */
	@JvmStatic
	var insideLog = false

	fun resetServerKnowledge() {
		serverSentUuid = false
	}

	@JvmStatic
	fun handleAddedMessage(message: Component, sender: PlayerInfo?): Component {
		lastSender = null

		val settings = config
		if (!settings.enabled) return message

		var playerInfo = sender
		if (settings.senderDetection == SenderDetection.HEURISTIC_ONLY || isShowcaseItemMessage(message)) {
			playerInfo = null
		} else if (playerInfo != null) {
			serverSentUuid = true
		} else if (settings.senderDetection == SenderDetection.UUID_ONLY) {
			return message
		} else if (serverSentUuid && settings.smartHeuristics && !Hypixel.isConnected) {
			// the server reports senders, so a message without one is not from a player
			return message
		}

		val (decorated, head) = decorate(message, playerInfo) ?: return message
		lastSender = head

		// "before line" heads are added in ChatComponentMixin, after other mods (timestamps...) changed the message
		return if (settings.position == HeadPosition.BEFORE_LINE) message else decorated
	}

	@JvmStatic
	fun handleSystemMessage(message: Component): Component =
		if (config.handleSystemMessages) handleAddedMessage(message, null) else message

	private fun decorate(message: Component, sender: PlayerInfo?): Pair<Component, HeadSource>? {
		// messages can arrive before login, e.g. with Polymer's early play networking
		val connection = Minecraft.getInstance().connection ?: return null

		val split = ComponentProcessor.split(message)
		if (ComponentProcessor.containsPlayerSprite(split)) return null

		if (sender == null && Hypixel.isConnected) {
			ComponentProcessor.addHeadAtStart(split) { text -> locateHypixelSender(connection, text) }
				?.let { return ComponentProcessor.join(split) to it }
		}

		val tellLookup = sender?.let { NameLookup.of(it, profileOnly = true) } ?: PlayerNameIndex.profileNames(connection)
		ComponentProcessor.addHeadForClickTell(split, tellLookup)
			?.let { return ComponentProcessor.join(split) to it }

		val nameLookup = sender?.let { NameLookup.of(it) } ?: PlayerNameIndex.allNames(connection)
		ComponentProcessor.addHeadForPlayerName(split, nameLookup)
			?.let { return ComponentProcessor.join(split) to it }

		if (sender != null) {
			val head = HeadSource.of(sender)
			return ComponentProcessor.prependHead(message, head) to head
		}

		return null
	}

	private fun locateHypixelSender(connection: ClientPacketListener, text: String): Pair<HeadSource, Int>? {
		val parsed = HypixelSenderParser.parse(text) ?: return null

		// players in the tab list have their exact skin (and nick skin) at hand
		PlayerNameIndex.findByProfileName(connection, parsed.name)?.let {
			return HeadSource.of(it) to parsed.headIndex
		}

		// guild/party/co-op members are usually somewhere else on the network: look them up by name
		if (parsed.trusted && config.resolveOfflinePlayers) {
			return HeadSource.byName(parsed.name) to parsed.headIndex
		}

		return null
	}

	private fun isShowcaseItemMessage(message: Component): Boolean =
		(message.contents as? TranslatableContents)?.key == "showcaseitem.misc.shared_item"

	private fun offsetEnabled(): Boolean = config.let { it.enabled && it.position == HeadPosition.BEFORE_LINE && it.offsetNonPlayerText }

	private fun startsWithHead(line: GuiMessage.Line): Boolean {
		var head = false
		line.content().accept { _, style, _ ->
			head = style.font is FontDescription.PlayerSprite
			false
		}
		return head
	}

	/** How far a line is moved right so text lines up with lines that start with a head. */
	@JvmStatic
	fun getChatOffset(line: GuiMessage.Line): Int = if (offsetEnabled() && !startsWithHead(line)) headWidth() else 0

	@JvmStatic
	fun getTextWidthDifference(): Int = if (offsetEnabled()) headWidth() else 0

	/** Pixels a head takes up, padding included. */
	@JvmStatic
	fun headWidth(): Int = 8 + extraAdvance()

	@JvmStatic
	fun extraAdvance(): Int = config.rightPadding + (2 * config.threeDeeNess).roundToInt()

	@JvmStatic
	fun threeDeeNess(): Float = config.threeDeeNess

	@JvmStatic
	fun drawShadow(): Boolean = config.drawShadow
}
