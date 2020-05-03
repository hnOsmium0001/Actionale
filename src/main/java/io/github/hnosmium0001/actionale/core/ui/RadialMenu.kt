package io.github.hnosmium0001.actionale.core.ui

import io.github.hnosmium0001.actionale.*
import io.github.hnosmium0001.actionale.core.action.Action
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.Vector3d
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import kotlin.math.cos
import kotlin.math.sin

const val PI = Math.PI
const val TWO_PI = Math.PI * 2
const val HALF_PI = Math.PI / 2

class RadialMenu(
    val components: Array<out Action>,
    val componentsPerPage: Int,
    title: Text
) : Screen(title) {
    constructor(
        components: Array<out Action>,
        componentsPerPage: Int,
        trKey: String
    ) : this(components, componentsPerPage, title = TranslatableText(trKey))

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground()
        renderRadialMenu(mouseX, mouseY)
        super.render(mouseX, mouseY, delta)
    }

    private fun renderRadialMenu(mouseX: Int, mouseY: Int) {
        val buffer = Tessellator.getInstance().buffer
        val mouse = Vector3d(mouseX.toDouble(), mouseY.toDouble(), 0.0)
        val center = Vector3d(width / 2.0, height / 2.0, 0.0)

        buffer.begin(GL_TRIANGLES, VertexFormats.POSITION_COLOR)

        val sides = components.size.coerceAtLeast(modConfig.radialMenuMinSides)
        val vertexAngle = TWO_PI / sides
        val flat = sides.isEven()
        // Right = 0, counter clock-wise = positive, in radians
        var angle = if (flat) vertexAngle / 2 else 0.0
        var lastVert = Vector3d(center.x + cos(angle), center.y + sin(angle), 0.0)
        var hoveredVert1: Vector3d? = null
        var hoveredVert2: Vector3d? = null
        for (i in (0..sides)) {
            angle += vertexAngle
            val vert = Vector3d(center.x + cos(angle), center.y + sin(angle), 0.0)
            if (mouse.pointInTriangle(center, lastVert, vert)) {
                hoveredVert1 = lastVert
                hoveredVert2 = vert
            }
            val col = if (i.isEven()) 0xaa1f6eff.toInt() else 0xaa1fcfff.toInt()
            buffer.vertex(center).color(col).next()
            buffer.vertex(lastVert).color(col).next()
            buffer.vertex(vert).color(col).next()

            components.getOrNull(i)?.run {
                // TODO draw component
                val itemAngle = angle - vertexAngle / 2
                val itemPos = Vector3d(center.x + cos(itemAngle), center.y + sin(itemAngle), 0.0)
            }

            lastVert = vert
        }

        if (hoveredVert1 != null && hoveredVert2 != null) {
            buffer.vertex(center).next()
            buffer.vertex(hoveredVert1).next()
            buffer.vertex(hoveredVert2).next()
        }

        buffer.end()
    }
}