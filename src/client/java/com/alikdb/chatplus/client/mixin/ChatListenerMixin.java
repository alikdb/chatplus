/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import com.alikdb.chatplus.client.mixininterface.Ownable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {
	// after filtering, either directly or after the chat delay
	@ModifyArg(
		method = "showMessageToPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
			ordinal = 0
		)
	)
	private Component chatplus$addHeadToPlayerMessage(Component message, @Local(argsOnly = true) PlayerChatMessage playerChatMessage) {
		// the sender UUID is more reliable than the GameProfile passed along
		return ChatHeads.handleAddedMessage(message, ((Ownable) (Object) playerChatMessage).chatplus_getOwner());
	}

	@ModifyArg(
		method = "lambda$handleDisguisedChatMessage$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
			ordinal = 0
		)
	)
	private Component chatplus$addHeadToDisguisedMessage(Component message) {
		return ChatHeads.handleAddedMessage(message, null);
	}

	// this is where all Hypixel chat arrives
	@ModifyArg(
		method = "handleSystemMessage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;addServerSystemMessage(Lnet/minecraft/network/chat/Component;)V",
			ordinal = 0
		)
	)
	private Component chatplus$addHeadToSystemMessage(Component message) {
		return ChatHeads.handleSystemMessage(message);
	}
}
