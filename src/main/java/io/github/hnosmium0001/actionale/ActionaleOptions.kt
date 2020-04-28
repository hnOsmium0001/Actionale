package io.github.hnosmium0001.actionale

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.hnosmium0001.actionale.core.action.ActionManager
import io.github.hnosmium0001.actionale.core.input.KeymapManager
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths

fun serializeModData(): JsonObject {
    return JsonObject().also { root ->
        root.add("keymaps", KeymapManager.serialize())
        root.add("radial_menu_actions", ActionManager.serializeRadialMenus())
    }
}

fun deserializeModData(data: JsonObject) {
    data.run {
        KeymapManager.deserialize(data.get("keymaps").asJsonArray)
        ActionManager.deserializeRadialMenus(data.get("radial_menu_actions").asJsonArray)
    }
}

val optionsPath get() = Paths.get("./options")
val playerOptionsPath = optionsPath.resolve("${Actionale.MODID}/player_options.json")

private val gson = GsonBuilder()
    .setPrettyPrinting()
    .create()
private val jsonParser = JsonParser()

fun writeModData() {
    val options = playerOptionsPath.toFile()
    options.parentFile.mkdirs()
    FileWriter(options).use { writer ->
        val modData = serializeModData()
        gson.toJson(modData, writer)
    }
}

fun readModData() {
    if (!Files.exists(playerOptionsPath)) {
        return
    }
    FileReader(playerOptionsPath.toFile()).use { reader ->
        val options = jsonParser.parse(reader).asJsonObject
        deserializeModData(options)
    }
}