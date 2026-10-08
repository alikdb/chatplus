/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.PlayerNameIndex;
import com.alikdb.chatplus.client.mixininterface.Ownable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

// priority 990: apply before EssentialClient, which wraps these calls in conditions
@Mixin(value = ClientPacketListener.class, priority = 990)
public abstract class ClientPacketListenerMixin {
	@Shadow
	public abstract @Nullable PlayerInfo getPlayerInfo(UUID uuid);

	@Inject(
		method = "handlePlayerChat",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handlePlayerChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/chat/ChatType$Bound;)V"
		)
	)
	private void chatplus$captureSender(ClientboundPlayerChatPacket packet, CallbackInfo ci, @Share("sender") LocalRef<PlayerInfo> sender) {
		sender.set(getPlayerInfo(packet.sender()));
	}

	@ModifyArg(
		method = "handlePlayerChat",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handlePlayerChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/chat/ChatType$Bound;)V"
		),
		index = 0
	)
	private PlayerChatMessage chatplus$rememberSender(PlayerChatMessage message, @Share("sender") LocalRef<PlayerInfo> sender) {
		((Ownable) (Object) message).chatplus_setOwner(sender.get());
		return message;
	}

	// keep the cached tab list name index in sync
	@Inject(method = {"handlePlayerInfoUpdate", "handlePlayerInfoRemove"}, at = @At("TAIL"))
	private void chatplus$playersChanged(CallbackInfo ci) {
		PlayerNameIndex.invalidate();
	}
}
