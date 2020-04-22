package io.github.hnosmium0001.actionale

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier

fun Identifier.toDotSeparated() = this.toString().replace(':', '.')

fun CompoundTag.putIdentifier(key: String, value: Identifier) {
    putString(key, value.toString())
}

fun CompoundTag.getIdentifier(key: String): Identifier? {
    val text = this.getString(key)
    return if (text.isEmpty()) null else Identifier.tryParse(text)
}

inline fun <T, C : Tag> Iterable<T>.pack(packer: (T) -> C) =
    ListTag().also { list ->
        for (element in this) {
            list.add(packer.invoke(element))
        }
    }

inline fun <T, C : Tag> Array<T>.pack(packer: (T) -> C) =
    ListTag().also { list ->
        for (element in this) {
            list.add(packer.invoke(element))
        }
    }

inline fun <K, V, C : Tag> Map<K, V>.packMap(packer: (V) -> C) =
    CompoundTag().also { compound ->
        for ((key, value) in this.entries) {
            compound.put(key.toString(), packer.invoke(value))
        }
    }

inline fun <T, S : MutableCollection<T>, reified C : Tag> ListTag.unpack(result: S, unpacker: (C) -> T) =
    result.also {
        for (compound in this) {
            result.add(unpacker.invoke(compound as C))
        }
    }

inline fun <reified T, reified C : Tag> ListTag.unpackArray(unpacker: (C) -> T) =
    Array(this.size) { idx ->
        unpacker.invoke(this[idx] as C)
    }

inline fun <T> ListTag.unpackUse(unpacker: CompoundTag.() -> T) {
    for (compound in this) {
        unpacker.invoke(compound as CompoundTag)
    }
}