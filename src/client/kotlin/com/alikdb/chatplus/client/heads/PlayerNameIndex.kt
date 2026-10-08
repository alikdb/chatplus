/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.heads

import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo

/** Names that can be found inside chat messages, grouped by first code point for fast scanning. */
class NameLookup(val names: Map<String, PlayerInfo>) {
	val byFirstCodePoint: Map<Int, List<String>> = names.keys.groupBy { it.codePointAt(0) }

	operator fun get(name: String): PlayerInfo? = names[name]

	companion object {
		val EMPTY = NameLookup(emptyMap())

		fun of(playerInfo: PlayerInfo, profileOnly: Boolean = false): NameLookup {
			val names = LinkedHashMap<String, PlayerInfo>()
			PlayerNameIndex.addProfileName(names, playerInfo)
			if (!profileOnly) PlayerNameIndex.addDisplayName(names, playerInfo)
			return NameLookup(names)
		}
	}
}

/**
 * Tab list names, rebuilt only when the player list changed (chat_heads rebuilds it for every message).
 * Invalidated from ClientPacketListenerMixin; everything runs on the client thread.
 */
object PlayerNameIndex {
	private val FORMATTING = Regex("§.")

	private var dirty = true
	private var owner: ClientPacketListener? = null

	private var profileNamesLower: Map<String, PlayerInfo> = emptyMap()
	private var profileLookup = NameLookup.EMPTY
	private var allLookup = NameLookup.EMPTY

	@JvmStatic
	fun invalidate() {
		dirty = true
	}

	fun profileNames(connection: ClientPacketListener): NameLookup = ensure(connection).let { profileLookup }

	fun allNames(connection: ClientPacketListener): NameLookup = ensure(connection).let { allLookup }

	fun findByProfileName(connection: ClientPacketListener, name: String): PlayerInfo? =
		ensure(connection).let { profileNamesLower[name.lowercase()] }

	private fun ensure(connection: ClientPacketListener) {
		if (!dirty && owner === connection) return

		val players = connection.onlinePlayers.filter(::isRealPlayer)

		val profileNames = LinkedHashMap<String, PlayerInfo>()
		players.forEach { addProfileName(profileNames, it) }

		val allNames = LinkedHashMap(profileNames)
		players.forEach { addDisplayName(allNames, it) }

		profileNamesLower = profileNames.mapKeys { it.key.lowercase() }
		profileLookup = NameLookup(profileNames)
		allLookup = NameLookup(allNames)
		owner = connection
		dirty = false
	}

	// TAB plugin layouts add fake "|slot_NN" entries; Hypixel SkyBlock fills its tab list with "!A-a" style ones
	private fun isRealPlayer(info: PlayerInfo): Boolean {
		val name = info.profile.name()
		return !name.startsWith("|slot_") && !name.startsWith("!")
	}

	internal fun addProfileName(target: MutableMap<String, PlayerInfo>, info: PlayerInfo) {
		// plugins like HaoNick can put formatting codes into profile names
		val name = FORMATTING.replace(info.profile.name(), "")
		if (name.isNotEmpty()) target[name] = info
	}

	internal fun addDisplayName(target: MutableMap<String, PlayerInfo>, info: PlayerInfo) {
		val displayName = info.tabListDisplayName ?: return
		val name = FORMATTING.replace(displayName.string, "")
		if (name.isNotBlank()) target.putIfAbsent(name, info)
	}
}
