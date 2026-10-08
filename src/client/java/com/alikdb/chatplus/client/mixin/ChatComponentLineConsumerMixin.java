/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** "Before line": move lines without a head right so all text lines up. Targets the line-drawing LineConsumer. */
@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$1")
public abstract class ChatComponentLineConsumerMixin {
	@Unique
	private static final String HANDLE_MESSAGE = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;handleMessage(IFLnet/minecraft/util/FormattedCharSequence;)Z";
	@Unique
	private static final String HANDLE_TAG_ICON = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;handleTagIcon(IIZLnet/minecraft/client/multiplayer/chat/GuiMessageTag;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag$Icon;)V";

	@Inject(method = "accept", at = @At(value = "INVOKE", target = HANDLE_MESSAGE))
	private void chatplus$offsetLine(CallbackInfo ci, @Local(argsOnly = true) GuiMessage.Line line, @Share("offset") LocalIntRef offset) {
		offset.set(ChatHeads.getChatOffset(line));
		translate(offset.get());
	}

	@Inject(method = "accept", at = @At(value = "INVOKE", target = HANDLE_MESSAGE, shift = At.Shift.AFTER))
	private void chatplus$undoLineOffset(CallbackInfo ci, @Share("offset") LocalIntRef offset) {
		translate(-offset.get());
	}

	@Inject(method = "accept", at = @At(value = "INVOKE", target = HANDLE_TAG_ICON))
	private void chatplus$offsetTagIcon(CallbackInfo ci, @Share("offset") LocalIntRef offset) {
		translate(offset.get());
	}

	@Inject(method = "accept", at = @At(value = "INVOKE", target = HANDLE_TAG_ICON, shift = At.Shift.AFTER))
	private void chatplus$undoTagIconOffset(CallbackInfo ci, @Share("offset") LocalIntRef offset) {
		translate(-offset.get());
	}

	@Unique
	private static void translate(int x) {
		ChatComponent.ChatGraphicsAccess graphics = ChatHeads.getChatGraphicsAccess();
		if (x != 0 && graphics != null) {
			graphics.updatePose(pose -> pose.translate(x, 0));
		}
	}
}
