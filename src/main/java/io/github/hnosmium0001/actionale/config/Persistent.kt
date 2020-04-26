package io.github.hnosmium0001.actionale.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.hnosmium0001.actionale.Actionale
import io.github.hnosmium0001.actionale.action.ActionManager
import io.github.hnosmium0001.actionale.input.KeymapManager
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

object GameOptions {
    val path get() = Paths.get("./options")
    val playerOptions = path.resolve("${Actionale.MODID}/player_options.json")
}

private val gson = GsonBuilder()
    .setPrettyPrinting()
    .create()
private val jsonParser = JsonParser()

fun writeModData() {
    val options = GameOptions.playerOptions.toFile()
    options.parentFile.mkdirs()
    FileWriter(options).use { writer ->
        val modData = serializeModData()
        gson.toJson(modData, writer)
    }
}

fun readModData() {
    if (!Files.exists(GameOptions.playerOptions)) {
        return
    }
    FileReader(GameOptions.playerOptions.toFile()).use { reader ->
        val options = jsonParser.parse(reader).asJsonObject
        deserializeModData(options)
    }
}