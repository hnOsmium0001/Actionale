package io.github.hnosmium0001.actionale.action

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding
import net.minecraft.client.options.KeyBinding

data class Category(
        val name: String
)

class Action(
        val name: String,
        val category: String,
        val keyChord: KeyChord
) {
    val callbacks = ArrayList<() -> Unit>()

    fun trigger() {
        for (callback in callbacks) {
            callback.invoke()
        }
    }
}

fun KeyBinding.toKeyChord() = KeyChord(Array(1) { defaultKeyCode })
fun KeyBinding.toAction() = Action(name, category, toKeyChord())
fun FabricKeyBinding.toAction() = Action(name, category, toKeyChord())