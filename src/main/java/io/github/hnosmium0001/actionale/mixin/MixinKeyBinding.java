package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.core.input.KeymapManager;
import io.github.hnosmium0001.actionale.integration.VanillaIntegrationKt;
import io.github.hnosmium0001.actionale.mixinextension.ExtendedKeyBinding;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements ExtendedKeyBinding {

    /**
     * Remove notification to vanilla key binds, this will be done through migrated keymaps.
     *
     * @author hnOsmium0001
     * @reason See body
     * @see io.github.hnosmium0001.actionale.integration.VanillaIntegrationKt#generateVanillaMigrations(KeyBinding[])
     */
    @Overwrite
    public static void onKeyPressed(InputUtil.KeyCode key) {
    }

    /**
     * Remove notification to vanilla key binds, compatibility will be done through keymap press/release events.
     * <p>
     * Note that we don't just direct this event because vanilla turns {@link org.lwjgl.glfw.GLFW#GLFW_REPEAT} events
     * into {@code pressed == true} which is not what we want (ignoring {@code GLFW_REPEAT} events completely).
     *
     * @author hnOsmium0001
     * @reason See body
     * @see io.github.hnosmium0001.actionale.integration.VanillaIntegrationKt#generateVanillaMigrations(KeyBinding[])
     */
    @Overwrite
    public static void setKeyPressed(InputUtil.KeyCode key, boolean pressed) {
    }

    @Shadow
    @Final
    private String id;
    @Shadow
    private int timesPressed;
    @Shadow
    private boolean pressed;

    /**
     * In case other developers tries to set the keycode of vanilla key binding manually, redirect the action to {@link
     * KeymapManager#getMigratedKeymaps()}
     */
    @Inject(method = "setKeyCode", at = @At("RETURN"))
    public void setKeyCodeHook(InputUtil.KeyCode newKey, CallbackInfo info) {
        VanillaIntegrationKt.updateMigration(id, newKey);
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
