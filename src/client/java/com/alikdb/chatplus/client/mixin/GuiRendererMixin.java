/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Head quads are built in GuiRenderer.prepare(), long after the chat was extracted. */
@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
	@Inject(method = "prepare", at = @At("HEAD"))
	private void chatplus$beginPrepare(CallbackInfo ci) {
		ChatHeads.setCustomHeadRendering(true);
	}

	@Inject(method = "prepare", at = @At("RETURN"))
	private void chatplus$endPrepare(CallbackInfo ci) {
		ChatHeads.setCustomHeadRendering(false);
	}
}
