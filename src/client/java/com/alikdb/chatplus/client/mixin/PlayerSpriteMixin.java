/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import com.alikdb.chatplus.client.mixininterface.VisibleInLog;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSprite.class)
public class PlayerSpriteMixin implements VisibleInLog {
	@Unique
	private boolean chatplus$visibleInLog = true;

	@Override
	public void chatplus_setVisibleInLog(boolean visibleInLog) {
		chatplus$visibleInLog = visibleInLog;
	}

	@Override
	public boolean chatplus_isVisibleInLog() {
		return chatplus$visibleInLog;
	}

	// our heads would show up as "[Name head]" in the log; only hide them there, other mods may rely on the text
	@Inject(method = "defaultFallback", at = @At("HEAD"), cancellable = true)
	private void chatplus$hideInLog(CallbackInfoReturnable<String> cir) {
		if (!chatplus$visibleInLog && ChatHeads.getInsideLog()) {
			cir.setReturnValue("");
		}
	}
}
