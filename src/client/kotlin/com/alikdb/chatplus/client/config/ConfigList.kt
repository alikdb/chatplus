package com.alikdb.chatplus.client.config

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

/** A scrolling list of widget rows, 310 pixels wide like the vanilla options screens. */
class ConfigList(minecraft: Minecraft, width: Int, height: Int, y: Int) :
	ContainerObjectSelectionList<ConfigList.Entry>(minecraft, width, height, y, ROW_HEIGHT) {

	init {
		centerListVertically = false
	}

	override fun getRowWidth() = ROW_WIDTH

	fun clear() = clearEntries()

	/** Widgets placed left to right; two default-sized widgets fill a row. */
	fun row(vararg widgets: AbstractWidget?) {
		val present = widgets.filterNotNull()
		var x = 0
		val placed = present.map { widget ->
			val placement = widget to x
			x += widget.width + if (present.size == 2 && widget.width == HALF) GAP_HALF else GAP
			placement
		}
		addEntry(WidgetRow(placed))
	}

	fun rowAt(vararg placed: Pair<AbstractWidget, Int>) {
		addEntry(WidgetRow(placed.toList()))
	}

	fun header(text: Component, font: Font) {
		val padding = if (children().isEmpty()) 0 else 8
		addEntry(HeaderRow(StringWidget(text, font), padding), padding + 9 + 6)
	}

	abstract class Entry : ContainerObjectSelectionList.Entry<Entry>()

	private class WidgetRow(private val widgets: List<Pair<AbstractWidget, Int>>) : Entry() {
		override fun extractContent(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, hovered: Boolean, a: Float) {
			for ((widget, offset) in widgets) {
				widget.setPosition(x + offset, contentY)
				widget.extractRenderState(graphics, mouseX, mouseY, a)
			}
		}

		override fun children(): List<GuiEventListener> = widgets.map { it.first }

		override fun narratables(): List<NarratableEntry> = widgets.map { it.first }
	}

	private class HeaderRow(private val widget: StringWidget, private val paddingTop: Int) : Entry() {
		override fun extractContent(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, hovered: Boolean, a: Float) {
			widget.setPosition(x, contentY + paddingTop)
			widget.extractRenderState(graphics, mouseX, mouseY, a)
		}

		override fun children(): List<GuiEventListener> = listOf(widget)

		override fun narratables(): List<NarratableEntry> = listOf(widget)
	}

	companion object {
		const val ROW_WIDTH = 310
		const val ROW_HEIGHT = 25
		const val HALF = 150
		private const val GAP_HALF = 10
		private const val GAP = 4
	}
}

/** Integer slider snapping to [step]; shows "Label: value". */
class IntSlider(
	width: Int,
	private val label: Component,
	private val min: Int,
	private val max: Int,
	private val step: Int,
	initial: Int,
	private val format: (Int) -> Component,
	private val onChange: (Int) -> Unit,
) : AbstractSliderButton(0, 0, width, 20, Component.empty(), (initial - min).toDouble() / (max - min)) {
	init {
		updateMessage()
	}

	private val current: Int
		get() = (min + Math.round(value * (max - min) / step).toInt() * step).coerceIn(min, max)

	override fun updateMessage() {
		message = CommonComponents.optionNameValue(label, format(current))
	}

	override fun applyValue() {
		onChange(current)
	}
}
