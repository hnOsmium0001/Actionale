package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.input.InputManager;
import io.github.hnosmium0001.actionale.input.MouseInputCallback;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(
            method = "onMouseButton",
            at = @At(value = "INVOKE", target = "setKeyPressed")
    )
    public void onMouseButton(long window, int button, int action, int mods) {
        MouseInputCallback.BUS.invoker().invoke(button, action, mods);
        InputManager.INSTANCE.setKeyStatus(InputUtil.Type.MOUSE.createFromCode(button), action);
    }
}
