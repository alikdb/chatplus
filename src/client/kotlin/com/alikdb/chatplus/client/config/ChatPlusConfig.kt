package com.alikdb.chatplus.client.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** One quick-switch button shown above the chat input. */
data class ChannelButton(
	var label: String = "",
	var command: String = "",
	var enabled: Boolean = true,
)

enum class ButtonAlignment { LEFT, RIGHT }

enum class HeadPosition { BEFORE_NAME, BEFORE_LINE }

enum class SenderDetection { UUID_AND_HEURISTIC, UUID_ONLY, HEURISTIC_ONLY }

class ChannelButtonsSettings {
	var enabled = true
	var onlyOnHypixel = true
	var syncWithServer = true
	var showSettingsButton = true
	var alignment = ButtonAlignment.LEFT
	/** 0 = fit to label */
	var buttonWidth = 0
	var buttons = defaultButtons()

	companion object {
		fun defaultButtons() = mutableListOf(
			ChannelButton("Normal", "/chat a"),
			ChannelButton("Party", "/chat p"),
			ChannelButton("Guild", "/chat g"),
			ChannelButton("Co-op", "/chat coop"),
			ChannelButton("Officer", "/chat o", enabled = false),
		)
	}
}

class ChatHeadsSettings {
	var enabled = true
	var position = HeadPosition.BEFORE_NAME
	var offsetNonPlayerText = true
	/** Fetch skins of players that are not in the tab list (guild/party/DM senders on Hypixel). */
	var resolveOfflinePlayers = true
	var handleSystemMessages = true
	var senderDetection = SenderDetection.UUID_AND_HEURISTIC
	var smartHeuristics = true
	var drawShadow = true
	var rightPadding = 1
	var threeDeeNess = 0f
	var commandSuggestionHeads = true
}

class ChatHistorySettings {
	var maxMessages = 1000
	var maxSentHistory = 500
	var keepOnDisconnect = false
}

class ChatPeekSettings {
	var enabled = true
	var toggleMode = false
	var fullHeight = true
	var scrollWithMouseWheel = true
}

class ChatPlusConfig {
	var channelButtons = ChannelButtonsSettings()
	var chatHeads = ChatHeadsSettings()
	var chatHistory = ChatHistorySettings()
	var chatPeek = ChatPeekSettings()

	companion object {
		const val MIN_HISTORY = 100
		const val MAX_HISTORY = 10000

		private val LOGGER = LoggerFactory.getLogger("chatplus-hypixel/config")
		private val GSON = GsonBuilder().setPrettyPrinting().create()
		private val PATH = FabricLoader.getInstance().configDir.resolve("chatplus-hypixel.json")

		@JvmStatic
		var instance = ChatPlusConfig()
			private set

		@JvmStatic
		fun get(): ChatPlusConfig = instance

		fun load() {
			if (Files.notExists(PATH)) {
				save()
				return
			}

			try {
				Files.newBufferedReader(PATH).use { reader ->
					instance = (GSON.fromJson(reader, ChatPlusConfig::class.java) ?: ChatPlusConfig()).sanitized()
				}
			} catch (e: Exception) {
				when (e) {
					is JsonParseException, is java.io.IOException -> {
						LOGGER.error("Could not read {}, using defaults", PATH, e)
						instance = ChatPlusConfig()
					}
					else -> throw e
				}
			}
		}

		fun save() {
			try {
				Files.createDirectories(PATH.parent)
				val tmp = PATH.resolveSibling(PATH.fileName.toString() + ".tmp")
				Files.newBufferedWriter(tmp).use { GSON.toJson(instance, it) }
				Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
			} catch (e: java.io.IOException) {
				LOGGER.error("Could not save {}", PATH, e)
			}
		}

		fun replace(config: ChatPlusConfig) {
			instance = config.sanitized()
			save()
		}
	}

	fun copy(): ChatPlusConfig = GSON.fromJson(GSON.toJson(this), ChatPlusConfig::class.java)

	// Gson bypasses Kotlin defaults for fields missing in older files, so null-check everything
	@Suppress("SENSELESS_COMPARISON")
	private fun sanitized(): ChatPlusConfig = apply {
		if (channelButtons == null) channelButtons = ChannelButtonsSettings()
		if (chatHeads == null) chatHeads = ChatHeadsSettings()
		if (chatHistory == null) chatHistory = ChatHistorySettings()
		if (chatPeek == null) chatPeek = ChatPeekSettings()

		with(channelButtons) {
			if (alignment == null) alignment = ButtonAlignment.LEFT
			if (buttons == null) buttons = ChannelButtonsSettings.defaultButtons()
			buttons.removeIf { it == null }
			buttons.forEach {
				if (it.label == null) it.label = ""
				if (it.command == null) it.command = ""
			}
			buttonWidth = buttonWidth.coerceIn(0, 200)
		}
		with(chatHeads) {
			if (position == null) position = HeadPosition.BEFORE_NAME
			if (senderDetection == null) senderDetection = SenderDetection.UUID_AND_HEURISTIC
			rightPadding = rightPadding.coerceIn(0, 4)
			threeDeeNess = threeDeeNess.coerceIn(0f, 1f)
		}
		with(chatHistory) {
			maxMessages = maxMessages.coerceIn(MIN_HISTORY, MAX_HISTORY)
			maxSentHistory = maxSentHistory.coerceIn(MIN_HISTORY, MAX_HISTORY)
		}
	}
}
