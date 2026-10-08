/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.font.PlainTextRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** "Render shadow" option for heads. */
@Mixin(PlainTextRenderable.class)
public interface PlainTextRenderableMixin {
	@Shadow
	int shadowColor();

	@ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/PlainTextRenderable;shadowColor()I"))
	default int chatplus$disableShadow(int original) {
		return ChatHeads.getCustomHeadRendering() && !ChatHeads.drawShadow() ? 0 : original;
	}

	// without its shadow the head looks one pixel too high next to shadowed text
	@ModifyArg(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/font/PlainTextRenderable;renderSprite(Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/vertex/VertexConsumer;IFFFI)V",
			ordinal = 1
		),
		index = 4
	)
	default float chatplus$lowerWithoutShadow(float offsetY) {
		return ChatHeads.getCustomHeadRendering() && !ChatHeads.drawShadow() && shadowColor() != 0 ? offsetY + 1 : offsetY;
	}
}
