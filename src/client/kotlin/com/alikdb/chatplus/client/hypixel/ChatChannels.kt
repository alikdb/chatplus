package com.alikdb.chatplus.client.hypixel

import com.alikdb.chatplus.client.config.ChatPlusConfig
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.StringUtil

/**
 * @property aliases accepted `/chat <alias>` arguments
 * @property serverNames how Hypixel names the channel in "You are now in the X channel", in English and Turkish
 */
enum class ChatChannel(private val aliases: Set<String>, private val serverNames: Set<String>) {
	ALL(setOf("a", "all"), setOf("ALL", "GENEL")),
	PARTY(setOf("p", "party"), setOf("PARTY", "PARTİ", "PARTI")),
	GUILD(setOf("g", "guild"), setOf("GUILD", "LONCA")),
	OFFICER(setOf("o", "officer"), setOf("OFFICER", "YETKİLİ", "YETKILI", "SUBAY")),
	COOP(setOf("coop", "co-op", "skyblock-coop"), setOf("SKYBLOCK CO-OP", "CO-OP", "COOP"));

	companion object {
		fun byAlias(alias: String): ChatChannel? {
			val key = alias.lowercase()
			return entries.firstOrNull { key in it.aliases }
		}

		fun byServerName(name: String): ChatChannel? {
			val key = name.trim()
			if ("CO-OP" in key || "COOP" in key) return COOP
			return entries.firstOrNull { key in it.serverNames }
		}
	}
}

object ChatChannels {
	private val CHAT_COMMAND = Regex("""^/?chat\s+(\S+)\s*$""", RegexOption.IGNORE_CASE)
	// "You are now in the PARTY channel", "...moved to the ALL channel.", "...moved back to the ALL channel."
	// and the Turkish "GENEL kanala taşındınız": the channel name is the upper-case run before "channel"/"kanal"
	private val SWITCHED = Regex("""(\p{Lu}[\p{Lu} -]*?) (?:channel|kanal)""")
	private val CONVERSATION = Regex("""^Opened a chat conversation with """)

	/** The channel Hypixel currently sends our messages to, as far as we know. */
	@JvmStatic
	var current: ChatChannel? = ChatChannel.ALL
		private set

	fun channelFor(command: String): ChatChannel? =
		CHAT_COMMAND.matchEntire(command.trim())?.let { ChatChannel.byAlias(it.groupValues[1]) }

	fun run(command: String) {
		val connection = Minecraft.getInstance().player?.connection ?: return
		val text = command.trim()
		if (text.isEmpty()) return

		if (text.startsWith("/")) {
			connection.sendCommand(text.substring(1))
		} else {
			connection.sendChat(text)
		}

		// optimistic, corrected by the server's confirmation if syncing is enabled
		channelFor(text)?.let { current = it }
	}

	fun onServerMessage(message: Component) {
		if (!Hypixel.isConnected || !ChatPlusConfig.get().channelButtons.syncWithServer) return

		val text = StringUtil.stripColor(message.string).trim()
		if (CONVERSATION.containsMatchIn(text)) {
			// "/chat <player>" opened a private conversation; none of the channel buttons is active
			current = null
			return
		}

		SWITCHED.find(text)?.let { match ->
			ChatChannel.byServerName(match.groupValues[1])?.let { current = it }
		}
	}

	fun reset() {
		current = ChatChannel.ALL
	}
}
