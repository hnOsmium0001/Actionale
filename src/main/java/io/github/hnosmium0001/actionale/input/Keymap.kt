package io.github.hnosmium0001.actionale.input

import com.google.common.base.Preconditions
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

class Keymap(val combination: Array<KeyChord>) {
    var state = GLFW_RELEASE
        private set
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
}

object KeymapManager {
    val keymaps: MutableSet<Keymap> = HashSet()
}
