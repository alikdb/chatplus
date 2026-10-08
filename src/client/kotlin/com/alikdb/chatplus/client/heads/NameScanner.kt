/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.heads

import net.minecraft.client.multiplayer.PlayerInfo

object NameScanner {
	/** Finds the first whole-word occurrence of a known name; returns the player and its code point index. */
	fun scan(message: String, lookup: NameLookup): Pair<PlayerInfo, Int>? {
		if (lookup.names.isEmpty()) return null

		val text = message.codePoints().toArray()
		var insideWord = false

		for (i in text.indices) {
			val c = text[i]

			// "tom" shouldn't match "custom"
			if (insideWord && isWordCharacter(c)) continue

			for (name in lookup.byFirstCodePoint[c].orEmpty()) {
				val nameSeq = name.codePoints().toArray()
				if (i + nameSeq.size > text.size) continue

				// "tom" shouldn't match "tomato"
				val endsAsWord = isWordCharacter(nameSeq.last())
				val followedByWord = i + nameSeq.size < text.size && isWordCharacter(text[i + nameSeq.size])
				if (endsAsWord && followedByWord) continue

				if (matchesAt(text, i, nameSeq)) {
					return lookup[name]?.let { it to i }
				}
			}

			insideWord = isWordCharacter(c)
		}

		return null
	}

	private fun isWordCharacter(codePoint: Int): Boolean =
		Character.isLetterOrDigit(codePoint) || codePoint == '_'.code || Character.getNumericValue(codePoint) != -1

	private fun matchesAt(text: IntArray, start: Int, name: IntArray): Boolean {
		for (j in name.indices) {
			if (text[start + j] != name[j]) return false
		}
		return true
	}
}
