package com.alikdb.chatplus.gametest

import com.alikdb.chatplus.client.config.ChatPlusConfig
import com.alikdb.chatplus.client.config.ChatPlusConfigScreen
import com.alikdb.chatplus.client.config.HeadPosition
import com.alikdb.chatplus.client.hypixel.ChatChannel
import com.alikdb.chatplus.client.hypixel.ChatChannels
import com.alikdb.chatplus.client.hypixel.Hypixel
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

/**
 * Pretends to be on Hypixel in a singleplayer world, feeds real-looking chat lines and takes screenshots
 * of the HUD chat, the open chat with channel buttons, the peek key and the settings screen.
 * Screenshots end up in build/run/clientGameTest/screenshots.
 */
object ChatPlusClientGameTest : FabricClientGameTest {
	private val LOGGER = LoggerFactory.getLogger("chatplus-hypixel-gametest")

	private val LINES = listOf(
		"§2Guild > §b[MVP§c+§b] blksx §e[Officer]§f: anyone up for F7?",
		"§9Party §8> §6[MVP§c++§6] jeb_§f: warping in 3",
		"§bCo-op > §7Dinnerbone§f: minions are full again",
		"§8[§b312§8] §6♫ §b[MVP§c+§b] Notch§f: selling enchanted books",
		"§dFrom §a[VIP§6+§a] Grumm§7: thanks for the carry!",
		"§2Guild > §aGrumm §ejoined.",
		"§f[NPC] Jacob§f: Welcome to the Jacob's Farming Contest!",
		"§aYou are now in the §6PARTY§a channel",
	)

	override fun runTest(context: ClientGameTestContext) {
		context.input.resizeWindow(1600, 900)
		context.setScreen { ChatPlusConfigScreen(null) }
		context.waitTicks(2)
		context.takeScreenshot("settings-channels").also { LOGGER.info("screenshot {}", it) }
		context.clickScreenButton("chatplus-hypixel.settings.tab.heads")
		context.waitTicks(2)
		context.takeScreenshot("settings-heads")
		context.clickScreenButton("chatplus-hypixel.settings.tab.chat")
		context.waitTicks(2)
		context.takeScreenshot("settings-chat")
		context.setScreen { null }

		context.worldBuilder().create().use { world ->
			world.connection.waitForChunksRender()

			context.runOnClient<RuntimeException> { minecraft ->
				pretendHypixel()
				LINES.forEach { line -> receive(minecraft, line) }
			}

			check(context.computeOnClient<ChatChannel?, RuntimeException> { ChatChannels.current } == ChatChannel.PARTY) { "channel sync did not switch to PARTY" }
			val heads = context.computeOnClient<Int, RuntimeException> { minecraft -> countHeads(minecraft) }
			LOGGER.info("lines with heads: {}", heads)
			check(heads == 6) { "expected 6 heads (no head for the NPC and the channel message), got $heads" }

			// skins of players that are not in the tab list are downloaded in the background
			context.waitTicks(100)
			context.takeScreenshot("chat-hud")

			context.setScreen { ChatScreen("", false) }
			context.waitTicks(5)
			context.takeScreenshot("chat-open")
			context.setScreen { null }

			context.waitTicks(20 * 11) // let the HUD chat fade out
			context.input.holdKey(GLFW.GLFW_KEY_LEFT_ALT)
			context.waitTicks(3)
			context.takeScreenshot("chat-peek")
			context.input.releaseKey(GLFW.GLFW_KEY_LEFT_ALT)

			context.runOnClient<RuntimeException> { minecraft ->
				ChatPlusConfig.get().chatHeads.position = HeadPosition.BEFORE_LINE
				receive(minecraft, "§2Guild > §b[MVP§c+§b] blksx §e[Officer]§f: heads at the start of the line")
				receive(minecraft, "§eLines without a head are aligned with the rest")
			}
			context.setScreen { ChatScreen("", false) }
			context.waitTicks(5)
			context.takeScreenshot("chat-before-line")
			context.setScreen { null }
			context.runOnClient<RuntimeException> { ChatPlusConfig.get().chatHeads.position = HeadPosition.BEFORE_NAME }
		}
	}

	private fun pretendHypixel() {
		val field = Hypixel::class.java.getDeclaredField("isConnected")
		field.isAccessible = true
		field.setBoolean(null, true)
	}

	private fun receive(minecraft: Minecraft, line: String) {
		val message = Component.literal(line)
		minecraft.gui.chatListener().handleSystemMessage(message, true)
		ChatChannels.onServerMessage(message)
	}

	private fun countHeads(minecraft: Minecraft): Int {
		val messages = minecraft.gui.hud.chat.storeState().let { state ->
			val field = state.javaClass.getDeclaredField("messages")
			field.isAccessible = true
			@Suppress("UNCHECKED_CAST")
			field.get(state) as List<net.minecraft.client.multiplayer.chat.GuiMessage>
		}

		return messages.count { message ->
			var head = false
			message.content().visit({ style, _ ->
				if (style.font is FontDescription.PlayerSprite) head = true
				java.util.Optional.empty<Unit>()
			}, net.minecraft.network.chat.Style.EMPTY)
			head
		}
	}
}
