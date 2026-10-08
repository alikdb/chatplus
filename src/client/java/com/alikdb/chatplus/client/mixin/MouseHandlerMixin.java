package com.alikdb.chatplus.client.mixin;

import com.alikdb.chatplus.client.peek.ChatPeek;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	// only reached in game (no screen open): scroll the peeked chat instead of the hotbar
	@Inject(
		method = "onScroll",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;onMouseScroll(DD)Lorg/joml/Vector2i;"),
		cancellable = true
	)
	private void chatplus$scrollPeekedChat(long handle, double xoffset, double yoffset, CallbackInfo ci) {
		if (ChatPeek.onScroll(yoffset)) {
			ci.cancel();
		}
	}
}
