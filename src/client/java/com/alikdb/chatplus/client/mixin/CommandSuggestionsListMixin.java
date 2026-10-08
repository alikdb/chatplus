/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.config.ChatPlusConfig;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** Heads next to player names in tab completion. */
@Mixin(CommandSuggestions.SuggestionsList.class)
public abstract class CommandSuggestionsListMixin {
	@Unique
	private static final int HEAD_SPACE = 2 + 8 + 1;

	@Shadow @Final
	private Rect2i rect;
	@Shadow @Final
	private List<Suggestion> suggestionList;

	@Unique
	private static boolean enabled() {
		return ChatPlusConfig.get().getChatHeads().getEnabled() && ChatPlusConfig.get().getChatHeads().getCommandSuggestionHeads();
	}

	@Unique
	private static PlayerInfo playerFor(Suggestion suggestion) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		return connection == null || !enabled() ? null : connection.getPlayerInfo(suggestion.getText());
	}

	// move the list right if the heads would go off screen
	@Inject(method = "<init>", at = @At("RETURN"))
	private void chatplus$makeRoomForHeads(CallbackInfo ci) {
		if (rect.getX() - HEAD_SPACE >= 3) return;

		for (Suggestion suggestion : suggestionList) {
			if (playerFor(suggestion) != null) {
				rect.setPosition(3 + HEAD_SPACE, rect.getY());
				return;
			}
		}
	}

	@ModifyVariable(method = "extractRenderState", at = @At("HEAD"), argsOnly = true)
	private GuiGraphicsExtractor chatplus$captureGraphics(GuiGraphicsExtractor graphics, @Share("graphics") LocalRef<GuiGraphicsExtractor> graphicsRef) {
		graphicsRef.set(graphics);
		return graphics;
	}

	@ModifyVariable(method = "extractRenderState", at = @At("STORE"), ordinal = 0)
	private Suggestion chatplus$captureSuggestion(Suggestion suggestion, @Share("player") LocalRef<PlayerInfo> player) {
		player.set(playerFor(suggestion));
		return suggestion;
	}

	// the background of each row (the earlier fills are the scroll indicators)
	@ModifyArg(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", ordinal = 4), index = 0)
	private int chatplus$widenRowBackground(int x, @Share("player") LocalRef<PlayerInfo> player) {
		return player.get() != null ? x - HEAD_SPACE : x;
	}

	@ModifyArg(
		method = "extractRenderState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V", ordinal = 0),
		index = 3
	)
	private int chatplus$drawHead(int y, @Share("player") LocalRef<PlayerInfo> player, @Share("graphics") LocalRef<GuiGraphicsExtractor> graphics) {
		if (player.get() != null) {
			PlayerFaceExtractor.extractRenderState(graphics.get(), player.get().getSkin(), rect.getX() - 9, y, 8);
		}
		return y;
	}
}
