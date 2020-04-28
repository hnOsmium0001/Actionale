package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.input.InputManager;
import io.github.hnosmium0001.actionale.input.KeyInputCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.KeyCode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements ExtendedKeyBinding {

    // Remove notification to vanilla key binds, this will be done through migrated keymaps
    // See {@link io.github.hnosmium0001.actionale.input.Keymap#generateMigrations}
    @Overwrite
    public static void onKeyPressed(KeyCode keyCode) {
    }

    // Redirect updates to vanilla's KeyBinding's to keymaps
    // Same as #onKeyPressed, compatibility will be done through
    @Overwrite
    public static void setKeyPressed(KeyCode key, boolean pressed) {
        InputManager.INSTANCE.setKeyStatus(key, pressed ? GLFW_PRESS : GLFW_RELEASE);
    }

    @Shadow private int timesPressed;

    @Shadow
    public abstract void setPressed(boolean pressed);

    @Override
    public void press() {
        this.setPressed(true);
        this.timesPressed++;
    }

    @Override
    public void release() {
        this.setPressed(false);
    }
}
