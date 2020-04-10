package io.github.hnosmium0001.actionale.action

import io.github.hnosmium0001.actionale.toDotSeparated
import net.minecraft.util.Identifier

data class Category(
        val name: Identifier,
        val trKey: String = "action.categories.${name.toDotSeparated()}"
)

class Action(
        val name: Identifier,
        val category: Identifier,
        val keyChord: KeyChord,
        val trKey: String = "action.${name.toDotSeparated()}"
) {
    val callbacks = ArrayList<() -> Unit>()

    fun trigger() {
        for (callback in callbacks) {
            callback.invoke()
        }
    }
}

// TODO
//fun KeyBinding.toAction() = Action(id, KeyChord(), category)