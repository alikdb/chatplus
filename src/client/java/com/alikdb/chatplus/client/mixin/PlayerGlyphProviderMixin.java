/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.font.PlayerGlyphProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Configurable gap after heads in the chat. */
@Mixin(value = PlayerGlyphProvider.class, priority = 500)
public abstract class PlayerGlyphProviderMixin {
	@ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/font/GlyphInfo;simple(F)Lcom/mojang/blaze3d/font/GlyphInfo;"))
	private static GlyphInfo chatplus$paddedInChat(GlyphInfo original) {
		return () -> original.getAdvance() + (ChatHeads.getCustomHeadRendering() ? ChatHeads.extraAdvance() : 0);
	}
}
