/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.heads.ChatHeads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** "3Dness": draws the hat layer slightly bigger than the face. Called from GuiRenderer.prepare(). */
@Mixin(targets = "net.minecraft.client.gui.font.PlayerGlyphProvider$Instance")
public abstract class PlayerGlyphInstanceMixin {
	@Unique
	private static final String RENDER_QUAD = "Lnet/minecraft/client/gui/font/PlayerGlyphProvider$Instance;renderQuad(Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/vertex/VertexConsumer;IFFFFFIFFIIII)V";

	@Unique
	private static float threeDee() {
		return ChatHeads.getCustomHeadRendering() ? ChatHeads.threeDeeNess() : 0;
	}

	// face: move right to make room for the wider hat
	@ModifyArg(method = "renderSprite", at = @At(value = "INVOKE", target = RENDER_QUAD, ordinal = 0), index = 3)
	private float chatplus$faceLeft(float x0) {
		return x0 + threeDee();
	}

	@ModifyArg(method = "renderSprite", at = @At(value = "INVOKE", target = RENDER_QUAD, ordinal = 0), index = 4)
	private float chatplus$faceRight(float x1) {
		return x1 + threeDee();
	}

	// hat: wider (twice, since the face moved right) and taller in both directions
	@ModifyArg(method = "renderSprite", at = @At(value = "INVOKE", target = RENDER_QUAD, ordinal = 1), index = 4)
	private float chatplus$hatRight(float x1) {
		return x1 + 2 * threeDee();
	}

	@ModifyArg(method = "renderSprite", at = @At(value = "INVOKE", target = RENDER_QUAD, ordinal = 1), index = 5)
	private float chatplus$hatTop(float y0) {
		return y0 - threeDee();
	}

	@ModifyArg(method = "renderSprite", at = @At(value = "INVOKE", target = RENDER_QUAD, ordinal = 1), index = 6)
	private float chatplus$hatBottom(float y1) {
		return y1 + threeDee();
	}
}
