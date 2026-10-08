package com.alikdb.chatplus.client

import com.alikdb.chatplus.ChatPlus
import com.alikdb.chatplus.client.config.ChatPlusConfig
import com.alikdb.chatplus.client.config.ChatPlusConfigScreen
import com.alikdb.chatplus.client.heads.ChatHeads
import com.alikdb.chatplus.client.heads.PlayerNameIndex
import com.alikdb.chatplus.client.hypixel.ChatChannels
import com.alikdb.chatplus.client.hypixel.Hypixel
import com.alikdb.chatplus.client.peek.ChatPeek
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.KeyMapping

object ChatPlusClient : ClientModInitializer {
	private val KEY_CATEGORY = KeyMapping.Category.register(ChatPlus.id("main"))

	private lateinit var settingsKey: KeyMapping
	private var openSettings = false

	override fun onInitializeClient() {
		ChatPlusConfig.load()

		ChatPeek.register(KEY_CATEGORY)
		settingsKey = KeyMappingHelper.registerKeyMapping(
			KeyMapping("key.chatplus-hypixel.settings", InputConstants.UNKNOWN.value, KEY_CATEGORY)
		)

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			ChatPeek.tick(client)

			while (settingsKey.consumeClick()) openSettings = true
			if (openSettings) {
				openSettings = false
				client.gui.setScreen(ChatPlusConfigScreen(client.gui.screen()))
			}
		}

		ClientPlayConnectionEvents.JOIN.register { connection, _, client ->
			Hypixel.onJoin(connection, client)
			PlayerNameIndex.invalidate()
		}

		ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
			Hypixel.onDisconnect()
			ChatHeads.resetServerKnowledge()
		}

		ClientReceiveMessageEvents.GAME.register { message, overlay ->
			if (!overlay) ChatChannels.onServerMessage(message)
		}

		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			dispatcher.register(ClientCommands.literal("chatplus").executes {
				// the chat screen closes right after running the command, so open on the next tick
				openSettingsNextTick()
				1
			})
		}

		ChatPlus.LOGGER.info("ChatPlus - Hypixel loaded")
	}

	fun openSettingsNextTick() {
		openSettings = true
	}
}
