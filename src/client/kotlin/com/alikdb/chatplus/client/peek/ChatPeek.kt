package com.alikdb.chatplus.client.peek

import com.alikdb.chatplus.client.config.ChatPlusConfig
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW
import kotlin.math.sign

/**
 * League of Legends style chat peek: while the key is held (or after toggling it), the chat is shown
 * like an open chat — every line, full opacity — without opening the input box or taking focus,
 * so you can keep moving. The mouse wheel scrolls the chat instead of the hotbar meanwhile.
 */
object ChatPeek {
	private lateinit var key: KeyMapping
	private var toggled = false
	private var wasPeeking = false

	fun register(category: KeyMapping.Category) {
		key = KeyMappingHelper.registerKeyMapping(KeyMapping("key.hypixel-chatplus.peek", GLFW.GLFW_KEY_LEFT_ALT, category))
	}

	@JvmStatic
	fun isPeeking(): Boolean {
		val settings = ChatPlusConfig.get().chatPeek
		if (!settings.enabled || !::key.isInitialized) return false

		val minecraft = Minecraft.getInstance()
		if (minecraft.player == null || minecraft.gui.screen() != null) return false

		return if (settings.toggleMode) toggled else key.isDown
	}

	fun keyName(): Component = key.translatedKeyMessage

	@JvmStatic
	fun useFocusedHeight(): Boolean = ChatPlusConfig.get().chatPeek.fullHeight && isPeeking()

	fun tick(minecraft: Minecraft) {
		while (key.consumeClick()) {
			toggled = ChatPlusConfig.get().chatPeek.toggleMode && !toggled
		}

		val peeking = isPeeking()
		if (wasPeeking && !peeking) {
			minecraft.gui.hud.chat.resetChatScroll()
		}
		wasPeeking = peeking
	}

	/** Returns true if the scroll was used for the chat. */
	@JvmStatic
	fun onScroll(amount: Double): Boolean {
		if (!ChatPlusConfig.get().chatPeek.scrollWithMouseWheel || !isPeeking() || amount == 0.0) return false

		val minecraft = Minecraft.getInstance()
		val lines = if (minecraft.hasShiftDown()) 1 else 7
		minecraft.gui.hud.chat.scrollChat(amount.sign.toInt() * lines)
		return true
	}
}
