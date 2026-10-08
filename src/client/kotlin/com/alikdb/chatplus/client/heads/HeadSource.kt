/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.heads

import com.alikdb.chatplus.client.mixininterface.VisibleInLog
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.objects.PlayerSprite
import net.minecraft.world.item.component.ResolvableProfile

/**
 * Whose head to draw. Players in the tab list carry their full profile (skin included),
 * everyone else is looked up by name: vanilla resolves the name to a UUID and skin
 * asynchronously (tab list first, then Mojang, cached in usercache.json) and shows a
 * default skin until it is loaded.
 */
class HeadSource private constructor(val profile: ResolvableProfile, val hat: Boolean) {
	fun createComponent(): MutableComponent {
		val sprite = PlayerSprite(profile, hat)
		(sprite as Any as VisibleInLog).chatplus_setVisibleInLog(false)
		return Component.`object`(sprite).withStyle(ChatFormatting.WHITE)
	}

	fun createComponent(neighbour: Component): MutableComponent {
		val head = createComponent()
		return if (neighbour.style.isStrikethrough) head.withStyle(ChatFormatting.STRIKETHROUGH) else head
	}

	companion object {
		@JvmStatic
		fun of(playerInfo: PlayerInfo) = HeadSource(ResolvableProfile.createResolved(playerInfo.profile), playerInfo.showHat())

		@JvmStatic
		fun byName(name: String) = HeadSource(ResolvableProfile.createUnresolved(name), true)
	}
}
