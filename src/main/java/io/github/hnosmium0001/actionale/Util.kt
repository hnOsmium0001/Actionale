package io.github.hnosmium0001.actionale

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.util.Identifier

fun Identifier.toDotSeparated() = this.toString().replace(':', '.')

fun JsonObject.addProperty(key: String, value: Identifier) {
    addProperty(key, value.toString())
}

val JsonElement.asIdentifier get() = Identifier(this.asString)

inline fun <T, C : JsonElement> Iterable<T>.pack(packer: (T) -> C) =
    JsonArray().also { array ->
        for (element in this) {
            array.add(packer.invoke(element))
        }
    }

inline fun <T, C : JsonElement> Array<T>.pack(packer: (T) -> C) =
    JsonArray().also { list ->
        for (element in this) {
            list.add(packer.invoke(element))
        }
    }

inline fun <K, V, C : JsonElement> Map<K, V>.packMap(packer: (K, V) -> C) =
    JsonArray().also { obj ->
        for ((key, value) in this.entries) {
            val entry = JsonObject().apply {
                addProperty("key", key.toString())
                add("value", packer.invoke(key, value))
            }
            obj.add(entry)
        }
    }

inline fun <T, S : MutableCollection<T>> JsonArray.unpack(result: S, unpacker: (JsonElement) -> T) =
    result.also {
        for (obj in this) {
            result.add(unpacker.invoke(obj))
        }
    }

inline fun <reified T> JsonArray.unpackArray(unpacker: (JsonElement) -> T) =
    Array(this.size()) { idx ->
        unpacker.invoke(this[idx])
    }

inline fun <T> JsonArray.unpackUse(unpacker: JsonObject.() -> T) {
    for (obj in this) {
        unpacker.invoke(obj as JsonObject)
    }
}