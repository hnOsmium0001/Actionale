package io.github.hnosmium0001.actionale.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface KeyInputCallback {

    public static Event<KeyInputCallback> EVENT = EventFactory.createArrayBacked(
            KeyInputCallback.class,
            listeners -> (keyCode, scancode, action, mods) -> {
                for (KeyInputCallback listener : listeners) {
                    listener.invoke(keyCode, scancode, action, mods);
                }
            });

    void invoke(int keyCode, int scancode, int action, int mods);
}
