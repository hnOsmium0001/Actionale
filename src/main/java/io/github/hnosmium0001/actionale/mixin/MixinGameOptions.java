package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.ActionaleOptionsKt;
import io.github.hnosmium0001.actionale.core.input.KeymapManager;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Shadow
    @Final
    private KeyBinding[] keysAll;

    @Inject(method = "load", at = @At("RETURN"))
    public void loadHook(CallbackInfo info) {
        ActionaleOptionsKt.readModData();
        KeymapManager.INSTANCE.generateMigrations(keysAll);
    }
}
