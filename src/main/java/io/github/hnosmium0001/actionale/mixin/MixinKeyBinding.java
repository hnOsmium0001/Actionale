package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.core.input.*;
import io.github.hnosmium0001.actionale.mixinextension.ExtendedKeyBinding;
import kotlin.Pair;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.KeyCode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements ExtendedKeyBinding {

    /**
     * Remove notification to vanilla key binds, this will be done through migrated keymaps. See {@link
     * KeymapManager#generateMigrations()}.
     */
    @Overwrite
    public static void onKeyPressed(KeyCode keyCode) {
    }

    /**
     * Redirect updates to vanilla's KeyBinding's to keymaps Same as #onKeyPressed, compatibility will be done through
     * keymap press/release events.
     */
    @Overwrite
    public static void setKeyPressed(KeyCode key, boolean pressed) {
        InputManager.INSTANCE.setKeyStatus(key, pressed ? GLFW_PRESS : GLFW_RELEASE);
    }

    @Shadow
    @Final
    private String id;
    @Shadow private int timesPressed;
    @Shadow private boolean pressed;

    /**
     * In case other developers tries to set the keycode of vanilla key binding manually, redirect the action to {@link
     * KeymapManager#getMigratedKeymaps()}
     */
    @Inject(method = "setKeyCode", at = @At("RETURN"))
    public void setKeyCodeHook(KeyCode newKey, CallbackInfo info) {
        Pair<KeyBinding, Keymap> queryResult = KeymapManager.INSTANCE.getMigratedKeymaps().get(id);
        if (queryResult == null) return;

        Keymap original = queryResult.getSecond();
        Keymap updated = new Keymap(
                original.getName(), original.getType(),
                new KeyChord[]{KeyChordManager.INSTANCE.obtain(newKey)}
        );
        KeymapManager.INSTANCE.getMigratedKeymaps().put(id, new Pair<>(queryResult.getFirst(), updated));
    }

    @Override
    public void press() {
        pressed = true;
        timesPressed++;
    }

    @Override
    public void release() {
        pressed = false;
    }
}
