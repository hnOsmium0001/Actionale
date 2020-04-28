package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.input.KeyInputCallback;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(
            method = "onKey",
            at = @At(value = "INVOKE", target = "net.minecraft.client.options.KeyBinding.setKeyPressed(Lnet/minecraft/client/util/InputUtil$KeyCode;Z)V")
    )
    public void onKeyHook(long window, int keyCode, int scancode, int action, int mods, CallbackInfo info) {
        KeyInputCallback.EVENT.invoker().invoke(keyCode, scancode, action, mods);
    }
}
