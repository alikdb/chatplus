/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.config.ChatPlusConfig;
import com.alikdb.chatplus.client.config.HeadPosition;
import com.alikdb.chatplus.client.heads.ChatHeads;
import com.alikdb.chatplus.client.heads.ComponentProcessor;
import com.alikdb.chatplus.client.heads.HeadSource;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// high priority number: run after mods like Chat Timestamps changed the message, so "before line" really is first
@Mixin(value = ChatComponent.class, priority = 10100)
public abstract class ChatComponentHeadsMixin {
	@ModifyArg(
		method = "addMessage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/chat/GuiMessage;<init>(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"
		),
		index = 1
	)
	private Component chatplus$prependHead(Component contents) {
		HeadSource sender = ChatHeads.getLastSender();
		if (sender != null && ChatPlusConfig.get().getChatHeads().getPosition() == HeadPosition.BEFORE_LINE) {
			return ComponentProcessor.INSTANCE.prependHead(contents, sender);
		}
		return contents;
	}

	@Inject(method = "addMessage", at = @At("RETURN"))
	private void chatplus$forgetSender(CallbackInfo ci) {
		ChatHeads.setLastSender(null);
	}

	@WrapMethod(method = "logChatMessage")
	private void chatplus$markInsideLog(GuiMessage message, Operation<Void> original) {
		ChatHeads.setInsideLog(true);
		try {
			original.call(message);
		} finally {
			ChatHeads.setInsideLog(false);
		}
	}

	@Inject(
		method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
		at = @At("HEAD")
	)
	private void chatplus$beginChatRender(CallbackInfo ci, @Local(argsOnly = true) ChatComponent.ChatGraphicsAccess graphics) {
		ChatHeads.setChatGraphicsAccess(graphics);
		ChatHeads.setCustomHeadRendering(true);
	}

	@Inject(
		method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
		at = @At("RETURN")
	)
	private void chatplus$endChatRender(CallbackInfo ci) {
		ChatHeads.setChatGraphicsAccess(null);
		ChatHeads.setCustomHeadRendering(false);
	}

	// widen the line backgrounds by the amount text was moved right in "before line" mode
	@ModifyArg(
		method = "lambda$extractRenderState$1",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"
		),
		index = 2
	)
	private static int chatplus$widenBackground(int right) {
		return right + ChatHeads.getTextWidthDifference();
	}
}
