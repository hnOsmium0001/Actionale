package io.github.hnosmium0001.actionale.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface MouseInputCallback {

    public static Event<MouseInputCallback> EVENT = EventFactory.createArrayBacked(
            MouseInputCallback.class,
            listeners -> (button, action, mods) -> {
                for (MouseInputCallback listener : listeners) {
                    listener.invoke(button, action, mods);
                }
            });

    void invoke(int button, int action, int mods);
}
