package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.input.InputManager;
import io.github.hnosmium0001.actionale.input.KeyInputCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(
            method = "onKey",
            at = @At(value = "INVOKE", target = "setKeyPressed")
    )
    public void onKey(long window, int keyCode, int scancode, int action, int mods) {
        KeyInputCallback.BUS.invoker().invoke(keyCode, scancode, action, mods);
        InputManager.INSTANCE.setKeyStatus(InputUtil.getKeyCode(keyCode, scancode), action);
    }
}
