package io.github.hnosmium0001.actionale;

import io.github.hnosmium0001.actionale.input.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class ActionaleInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        KeyInputCallback.BUS.register(InputManager.INSTANCE::keyStatusCallback);
        MouseInputCallback.BUS.register(InputManager.INSTANCE::mouseStatusCallback);
    }
}
