package io.github.hnosmium0001.actionale

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.hnosmium0001.actionale.core.action.ActionManager
import io.github.hnosmium0001.actionale.core.input.KeymapManager
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun serializeDefaultInputMap(): JsonObject {
    return JsonObject().also { root ->
        // TODO
    }
}

// Default input map is a write-only file
@Suppress("unused")
fun deserializeDefaultInputMap(data: JsonObject) {
}

fun serializeUserInputMap(): JsonObject {
    return JsonObject().also { root ->
        root.add("keymaps", KeymapManager.serializeOverrides())
        root.add("simple_keymaps", KeymapManager.serializeSimpleOverrides())
        root.add("radial_menu_actions", ActionManager.serializeRadialMenus())
    }
}

fun deserializeUserInputMap(data: JsonObject) {
    data.run {
        KeymapManager.deserializeOverrides(data.get("keymaps").asJsonArray)
        KeymapManager.deserializeSimpleOverrides(data.get("simple_keymaps").asJsonArray)
        ActionManager.deserializeRadialMenus(data.get("radial_menu_actions").asJsonArray)
    }
}

val optionsDirectory get() = Paths.get("./options")
val defaultPath = optionsDirectory.resolve("${Actionale.MODID}/default_input_map.json")
val userPath = optionsDirectory.resolve("${Actionale.MODID}/user_input_map.json")

private val gson = GsonBuilder()
    .setPrettyPrinting()
    .create()
private val jsonParser = JsonParser()

private fun makeFileWriter(path: Path): FileWriter {
    val file = path.toFile()
    file.mkdirs()
    return FileWriter(file)
}

fun writeModData() {
    makeFileWriter(defaultPath).use { writer ->
        gson.toJson(serializeDefaultInputMap(), writer)
    }

    makeFileWriter(userPath).use { writer ->
        gson.toJson(serializeUserInputMap(), writer)
    }
}

fun readModData() {
    if (Files.exists(userPath)) {
        FileReader(userPath.toFile()).use { reader ->
            deserializeUserInputMap(jsonParser.parse(reader).asJsonObject)
        }
    }
}