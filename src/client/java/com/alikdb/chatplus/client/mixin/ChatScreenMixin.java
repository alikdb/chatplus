package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.channel.ChannelBar;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Channel buttons above the chat input. */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
	@Shadow
	protected EditBox input;

	@Unique
	private @Nullable ChannelBar chatplus$channelBar;

	protected ChatScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void chatplus$createChannelBar(CallbackInfo ci) {
		chatplus$channelBar = new ChannelBar(this.font, this.width, this.height);
	}

	// drawn before the command suggestions so those stay on top
	@Inject(
		method = "extractRenderState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
	)
	private void chatplus$drawChannelBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (chatplus$channelBar != null && chatplus$channelBar.isVisible(input.getValue())) {
			chatplus$channelBar.extractRenderState(graphics, mouseX, mouseY);
		}
	}

	// after open suggestions had their chance, before chat text and widgets
	@ModifyExpressionValue(
		method = "mouseClicked",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;)Z")
	)
	private boolean chatplus$clickChannelBar(boolean handledBySuggestions, @Local(argsOnly = true) MouseButtonEvent event) {
		if (handledBySuggestions || event.button() != 0 || chatplus$channelBar == null || !chatplus$channelBar.isVisible(input.getValue())) {
			return handledBySuggestions;
		}
		return chatplus$channelBar.mouseClicked(event.x(), event.y());
	}
}
