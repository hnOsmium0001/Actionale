package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.input.MouseInputCallback;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(
            method = "onMouseButton",
            at = @At(value = "INVOKE", target = "net.minecraft.client.options.KeyBinding.setKeyPressed(Lnet/minecraft/client/util/InputUtil$KeyCode;Z)V")
    )
    public void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo info) {
        MouseInputCallback.EVENT.invoker().invoke(button, action, mods);
    }
}
