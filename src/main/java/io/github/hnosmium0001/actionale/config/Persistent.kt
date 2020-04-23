package io.github.hnosmium0001.actionale.config

import com.google.gson.JsonObject
import io.github.hnosmium0001.actionale.action.ActionManager
import io.github.hnosmium0001.actionale.input.KeymapManager

fun serializeKeymaps(): JsonObject {
    return JsonObject().also { root ->
        root.add("keymaps", KeymapManager.serialize())
        root.add("radial_menu_actions", ActionManager.serializeRadialMenus())
    }
}

fun deserializeKeymaps(data: JsonObject) {
    data.run {
        KeymapManager.deserialize(data.get("keymaps").asJsonArray)
        ActionManager.deserializeRadialMenus(data.get("radial_menu_actions").asJsonArray)
    }
}