package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.config.ChatPlusConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Hud.class)
public abstract class HudMixin {
	@WrapWithCondition(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"))
	private boolean chatplus$keepChatOnDisconnect(ChatComponent chat, boolean history) {
		return !ChatPlusConfig.get().getChatHistory().getKeepOnDisconnect();
	}
}
