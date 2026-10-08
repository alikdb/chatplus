package com.alikdb.chatplus.client.channel

import com.alikdb.chatplus.client.HypixelChatPLUSClient
import com.alikdb.chatplus.client.config.ButtonAlignment
import com.alikdb.chatplus.client.config.ChannelButton
import com.alikdb.chatplus.client.config.ChatPlusConfig
import com.alikdb.chatplus.client.hypixel.ChatChannel
import com.alikdb.chatplus.client.hypixel.ChatChannels
import com.alikdb.chatplus.client.hypixel.Hypixel
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents

/**
 * The row of channel buttons drawn between the chat messages and the input box.
 * Not a widget on purpose: widgets take keyboard focus away from the chat input when clicked.
 */
class ChannelBar(private val font: Font, screenWidth: Int, screenHeight: Int) {
	private class Slot(val x: Int, val width: Int, val label: Component, val tooltip: Component?, val channel: ChatChannel?, val onClick: () -> Unit)

	private val slots = ArrayList<Slot>()
	private val top = screenHeight - INPUT_HEIGHT - GAP - HEIGHT

	init {
		val settings = ChatPlusConfig.get().channelButtons
		val buttons = settings.buttons.filter { it.enabled && it.label.isNotBlank() }

		val entries = buttons.map { button ->
			val width = if (settings.buttonWidth > 0) settings.buttonWidth else font.width(label(button)) + 2 * PADDING
			Triple(button, label(button), maxOf(width, MIN_WIDTH))
		}

		val settingsWidth = if (settings.showSettingsButton) HEIGHT else 0
		val total = entries.sumOf { it.third + SPACING } + settingsWidth
		var x = if (settings.alignment == ButtonAlignment.RIGHT) screenWidth - 2 - total else 2

		for ((button, label, width) in entries) {
			val command = button.command
			slots += Slot(x, width, label, Component.literal(command).withStyle(ChatFormatting.GRAY), ChatChannels.channelFor(command), {
				ChatChannels.run(command)
			})
			x += width + SPACING
		}

		if (settings.showSettingsButton) {
			slots += Slot(x, settingsWidth, Component.literal("⚙"), Component.translatable("hypixel-chatplus.settings.title"), null, {
				HypixelChatPLUSClient.openSettingsNextTick()
			})
		}
	}

	fun isVisible(input: String): Boolean {
		val settings = ChatPlusConfig.get().channelButtons
		// command hints and suggestions use the same space
		return settings.enabled && slots.isNotEmpty() && (!settings.onlyOnHypixel || Hypixel.isConnected) && !input.startsWith("/")
	}

	fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
		val current = ChatChannels.current

		for (slot in slots) {
			val hovered = contains(slot, mouseX, mouseY)
			val active = slot.channel != null && slot.channel == current
			drawButton(graphics, slot.x, top, slot.width, active, hovered)

			val color = if (active || hovered) 0xFFFFFFFF.toInt() else 0xFFC8D6E0.toInt()
			graphics.centeredText(font, slot.label, slot.x + slot.width / 2, top + (HEIGHT - 8) / 2 + 1, color)

			if (hovered && slot.tooltip != null) {
				graphics.setTooltipForNextFrame(font, slot.tooltip, mouseX, mouseY)
			}
		}
	}

	fun mouseClicked(mouseX: Double, mouseY: Double): Boolean {
		val slot = slots.firstOrNull { contains(it, mouseX.toInt(), mouseY.toInt()) } ?: return false
		Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
		slot.onClick()
		return true
	}

	private fun contains(slot: Slot, x: Int, y: Int) = x >= slot.x && x < slot.x + slot.width && y >= top && y < top + HEIGHT

	// pixel-art button in the style of the mockup: dark blue idle, light blue with a bright inner frame when active
	private fun drawButton(graphics: GuiGraphicsExtractor, x: Int, y: Int, width: Int, active: Boolean, hovered: Boolean) {
		val right = x + width
		val bottom = y + HEIGHT

		val fill = when {
			active -> ACTIVE_FILL
			hovered -> HOVER_FILL
			else -> IDLE_FILL
		}

		graphics.fill(x, y, right, bottom, OUTLINE)
		graphics.fill(x + 1, y + 1, right - 1, bottom - 1, fill)

		if (active || hovered) {
			val frame = if (active) ACTIVE_FRAME else HOVER_FRAME
			graphics.outline(x + 1, y + 1, width - 2, HEIGHT - 2, frame)
			// corner rivets
			graphics.fill(x + 2, y + 2, x + 3, y + 3, frame)
			graphics.fill(right - 3, y + 2, right - 2, y + 3, frame)
			graphics.fill(x + 2, bottom - 3, x + 3, bottom - 2, frame)
			graphics.fill(right - 3, bottom - 3, right - 2, bottom - 2, frame)
		} else {
			// bottom shade gives the idle buttons some depth
			graphics.fill(x + 1, bottom - 2, right - 1, bottom - 1, IDLE_SHADE)
		}
	}

	companion object {
		const val HEIGHT = 14
		private const val INPUT_HEIGHT = 14
		private const val GAP = 2
		private const val SPACING = 3
		private const val PADDING = 7
		private const val MIN_WIDTH = 24

		private val OUTLINE = 0xFF0E2233.toInt()
		private val IDLE_FILL = 0xFF1E4A66.toInt()
		private val IDLE_SHADE = 0xFF173A51.toInt()
		private val HOVER_FILL = 0xFF2B5F82.toInt()
		private val HOVER_FRAME = 0xFF3B7AA6.toInt()
		private val ACTIVE_FILL = 0xFF3C7299.toInt()
		private val ACTIVE_FRAME = 0xFF5CA3D6.toInt()

		/** `&` color codes are allowed in labels since § can't be typed. */
		fun label(button: ChannelButton): Component = Component.literal(button.label.replace(Regex("&([0-9a-fk-or])"), "§$1"))
	}
}
