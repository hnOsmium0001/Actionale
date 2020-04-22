package io.github.hnosmium0001.actionale.input

import com.google.common.base.Preconditions
import io.github.hnosmium0001.actionale.pack
import io.github.hnosmium0001.actionale.packMap
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

/**
 * A keymap is an ordered combination of different key chords. It is "pressed" when all of the key chords are pressed in
 * the given order, and released when any of the key chords are released.
 *
 * Keymaps have 3 categories:
 * 1. migrated KeyBinding keymaps: keymaps automatically generated from vanilla's KeyBinding. Can be added neither in
 * code nor in-game.
 * 2. developer-defined keymaps: keymaps created by mod developers, immutable and can't be changed by players
 * 3. player-defined keymaps (overrides): keymaps created by players, this will override both developer-defined keymaps
 * and migrated KeyBinding keymaps. This is the only keymaps that are persisted on disk.
 */
class Keymap(val combination: Array<KeyChord>) {
    /**
     * Listeners to be called whenever this keymap changes state, i.e. from `PRESSED` to `RELEASED` or vice versa. Add
     * or remove listeners according to the need.
     */
    val listeners: MutableSet<(Keymap, InputAction) -> Unit> = HashSet()
    var state: InputAction = GLFW_RELEASE
        private set(value) {
            if (field != value) {
                field = value
                for (listener in listeners) {
                    listener.invoke(this, value)
                }
            }
        }
    val pressed get() = state == GLFW_PRESS
    val released get() = state == GLFW_RELEASE

    init {
        Preconditions.checkArgument(combination.isNotEmpty())
        for (chord in combination) {
            chord.listeners += this::onChordChanged
        }
    }

    private var pressedIndex = -1
    private fun onChordChanged(chord: KeyChord, state: InputAction) {
        if (state == GLFW_RELEASE && combination.last() == chord) {
            // If the last key chord is released, this keymap must be released
            // because the last key chord must be the last to trigger and last to release
            this.state = GLFW_RELEASE
            return
        }

        if (combination[pressedIndex + 1] == chord) {
            // The next required key chord is pressed, advance index to look for the next one
            pressedIndex++
            if (pressedIndex >= combination.size) {
                // At end of the array, matched all of the key chords successfully
                this.state = state
            }
        } else {
            // Pressing streak failed, reset to beginning
            pressedIndex = -1
        }
    }

    override fun toString(): String {
        return combination.asSequence()
            .map { it.toString() }
            .joinToString { it }
    }

    fun translate(): String {
        return combination.asSequence()
            .map { it.translate() }
            .joinToString { it }
    }
}

fun Keymap.serialize() =
    this.combination.pack { chord ->
        // KeyChord[]
        chord.keys.pack { key ->
            // Key
            CompoundTag().apply {
                putInt("Type", key.category.ordinal)
                putInt("KeyCode", key.keyCode)
            }
            // ...
        }
        // ...
    }

fun deserializeKeymap(data: ListTag) =
    Keymap(Array(data.size) { chordIdx ->
        val keysData = data.getList(chordIdx)
        KeyChordManager.obtain(Array(keysData.size) { keyIdx ->
            val keyData = keysData.getCompound(keyIdx)
            val typeID = keyData.getInt("Type")
            val keyCode = keyData.getInt("KeyCode")
            InputUtil.Type.values()[typeID].createFromCode(keyCode)
        })
    })

object KeymapManager {
    /**
     * Keymaps automatically generated from [KeyBinding]s. Cannot be created manually but its generation can be
     * configured by TODO.
     */
    val migratedKeymaps: Map<String, Pair<KeyBinding, Keymap>> = HashMap()

    /**
     * Developer-defined immutable (in terms of players) keymaps. This may contain action-related keymaps *if* they are
     * defined by the developer, otherwise (defined by players) they will exist in [keymapOverrides]. Nor will these
     * keymaps be persisted.
     */
    val keymaps: Map<String, Keymap> = HashMap()

    /**
     * Player-defined key maps that are persisted onto the disk. The overrides has higher priority than the developer-
     * defined kaymaps when querying and deleting.
     */
    // Modified by users through GUI
    val keymapOverrides: MutableMap<String, Keymap> = HashMap()

    fun generateMigrations() {
        TODO("unimplemented")
    }

    fun registerKeymap(name: String, keymap: Keymap) {
        keymaps as MutableMap
        keymaps[name] = keymap
        // If the developer-defined keymap overrides a migrated keymap, we replaced the auto-generated one
        migratedKeymaps[name]?.let { old ->
            migratedKeymaps as MutableMap
            migratedKeymaps[name] = Pair(old.first, keymap)
        }
    }

    operator fun get(name: String): Keymap? {
        return keymapOverrides[name] ?: keymaps[name] ?: migratedKeymaps[name]?.second
    }

    /**
     * Attempt to remove a registered keymap. Try on user-defined overrides first, otherwise try on developer-defined
     * keymaps.
     */
    fun remove(name: String): Boolean {
        // Removing migrated keymaps is not allowed
        return if (keymapOverrides.remove(name) != null) {
            true
        } else {
            keymaps as MutableMap
            keymaps.remove(name) != null
        }
    }

    fun serializeOverrides(): CompoundTag {
        return keymapOverrides.packMap { keymap ->
            // (name, Keymap[])
            keymap.serialize()
            // ...
        }
    }

    fun deserializeOverrides(data: CompoundTag) {
        for (name in data.keys) {
            // Drop unmapped overrides
            if (!keymaps.contains(name)) {
                continue
            }
            keymapOverrides[name] = deserializeKeymap(data.getList(name, NbtType.COMPOUND))
        }
    }
}
