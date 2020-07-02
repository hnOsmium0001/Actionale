package io.github.hnosmium0001.actionale.core

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.hnosmium0001.actionale.core.input.Key
import io.github.hnosmium0001.actionale.core.input.categoryName
import io.github.hnosmium0001.actionale.core.input.indicator
import io.github.hnosmium0001.actionale.core.input.keyFrom
import io.github.hnosmium0001.actionale.modConfig
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import java.lang.IllegalStateException

inline fun <A, B> Pair<A, B>.firstOtherwise(otherwise: () -> Pair<A, B>): Pair<A, B> {
    return if (first != null) {
        this
    } else {
        otherwise.invoke()
    }
}

inline fun <A, B> Pair<A, B>.secondOtherwise(otherwise: () -> Pair<A, B>): Pair<A, B> {
    return if (second != null) {
        this
    } else {
        otherwise.invoke()
    }
}

inline fun <T, S : MutableList<T>> S.mapInPlace(mutator: (T) -> T): S {
    val it = this.listIterator()
    while (it.hasNext()) {
        val old = it.next()
        val new = mutator.invoke(old)
        if (old != new) {
            it.set(new)
        }
    }
    return this
}

fun Identifier.toDotSeparated() = this.toString().replace(':', '.')

fun Key.toJsonObject(): JsonObject {
    return JsonObject().also { json ->
        if (modConfig.exportNamedKeys) {
            json.addProperty("type", this.categoryName)
            json.addProperty("keycode", this.indicator)
        } else {
            json.addProperty("type", this.category.ordinal)
            json.addProperty("keycode", this.keyCode)
        }
    }
}

fun JsonObject.addProperty(key: String, value: Identifier) {
    this.addProperty(key, value.toString())
}

fun JsonObject.addProperty(key: String, value: BlockPos) {
    this.add(key, JsonArray().apply {
        add(value.x)
        add(value.y)
        add(value.z)
    })
}

fun JsonObject.addProperty(key: String, value: Key) {
    add(key, value.toJsonObject())
}

val JsonElement.asIdentifier: Identifier get() = Identifier(this.asString)
val JsonElement.asBlockPos: BlockPos
    get() {
        val array = this.asJsonArray
        return BlockPos(array[0].asInt, array[1].asInt, array[2].asInt)
    }
val JsonElement.asKey: Key
    get() {
        if (this !is JsonObject) {
            throw IllegalStateException("This is not a JSON Object, therefore not a JSON key.")
        }

        val typeJson = this.get("type").asJsonPrimitive
        val keyCodeJson = this.get("keycode").asJsonPrimitive

        return when {
            typeJson.isNumber && keyCodeJson.isNumber ->
                InputUtil.Type.values()[typeJson.asInt].createFromCode(keyCodeJson.asInt)
            typeJson.isString && keyCodeJson.isString ->
                keyFrom(typeJson.asString, keyCodeJson.asString)
            else ->
                throw IllegalStateException("This JSON key contains invalid `type` and `keycode` properties.")
        }
    }

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
            obj.add(packer.invoke(key, value))
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

inline fun <T> JsonArray.unpackUse(unpacker: (JsonObject) -> T) {
    for (obj in this) {
        unpacker.invoke(obj as JsonObject)
    }
}