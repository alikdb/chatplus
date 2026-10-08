package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.config.ChatPlusConfig;
import com.alikdb.chatplus.client.peek.ChatPeek;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.gui.components.ChatComponent;

/** Chat history size and chat peek. */
@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
	@ModifyConstant(method = {"addMessageToDisplayQueue", "addMessageToQueue"}, constant = @Constant(intValue = 100))
	private int chatplus$maxMessages(int vanilla) {
		return ChatPlusConfig.get().getChatHistory().getMaxMessages();
	}

	@ModifyConstant(method = "addRecentChat", constant = @Constant(intValue = 100))
	private int chatplus$maxSentMessages(int vanilla) {
		return ChatPlusConfig.get().getChatHistory().getMaxSentHistory();
	}

	// while peeking, draw every line fully opaque (like an open chat) but without the input box or hover effects
	@ModifyVariable(
		method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
		at = @At("STORE"),
		ordinal = 0
	)
	private boolean chatplus$peekAsForeground(boolean isForeground) {
		return isForeground || ChatPeek.isPeeking();
	}

	@ModifyExpressionValue(method = "getHeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z"))
	private boolean chatplus$peekHeight(boolean focused) {
		return focused || ChatPeek.useFocusedHeight();
	}
}
