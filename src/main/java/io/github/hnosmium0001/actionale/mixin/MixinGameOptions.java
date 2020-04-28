package io.github.hnosmium0001.actionale.mixin;

import io.github.hnosmium0001.actionale.config.PersistentKt;
import io.github.hnosmium0001.actionale.input.KeymapManager;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Inject(
            method = "load()V",
            at = @At("RETURN")
    )
    public void loadHook() {
        PersistentKt.readModData();
        KeymapManager.INSTANCE.generateMigrations();
    }
}
