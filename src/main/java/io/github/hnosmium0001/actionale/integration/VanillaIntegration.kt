package io.github.hnosmium0001.actionale.integration

import io.github.hnosmium0001.actionale.core.input.KeyChordManager
import io.github.hnosmium0001.actionale.core.input.Keymap
import io.github.hnosmium0001.actionale.core.input.KeymapManager
import io.github.hnosmium0001.actionale.core.input.KeymapType
import io.github.hnosmium0001.actionale.mixinextension.ExtendedKeyBinding
import net.minecraft.client.options.KeyBinding
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

/**
 * Generate and add migrated keymaps
 */
fun generateVanillaMigrations(keyBindings: Array<out KeyBinding>) {
    KeymapManager.migratedKeymaps as MutableMap
    for (keyBinding in keyBindings) {
        val migration = Keymap(
            name = keyBinding.id,
            type = KeymapType.MIGRATED,
            combination = arrayOf(KeyChordManager.obtain(keyBinding.defaultKeyCode))
        )
        migration.listeners += { _, action ->
            keyBinding as ExtendedKeyBinding
            when (action) {
                GLFW_PRESS -> keyBinding.press()
                GLFW_RELEASE -> keyBinding.release()
                else -> throw RuntimeException()
            }
        }
        KeymapManager.migratedKeymaps[keyBinding.id] = Pair(keyBinding, migration)
    }
}