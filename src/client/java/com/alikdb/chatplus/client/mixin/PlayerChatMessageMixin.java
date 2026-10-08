/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.mixininterface.Ownable;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerChatMessage.class)
public abstract class PlayerChatMessageMixin implements Ownable {
	@Unique
	private @Nullable PlayerInfo chatplus$owner;

	@Override
	public @Nullable PlayerInfo chatplus_getOwner() {
		return chatplus$owner;
	}

	@Override
	public void chatplus_setOwner(@Nullable PlayerInfo owner) {
		chatplus$owner = owner;
	}
}
