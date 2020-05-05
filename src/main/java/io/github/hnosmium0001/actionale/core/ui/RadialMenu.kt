package io.github.hnosmium0001.actionale.core.ui

import com.mojang.blaze3d.platform.GlStateManager.DstFactor
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor
import com.mojang.blaze3d.systems.RenderSystem
import io.github.hnosmium0001.actionale.core.*
import io.github.hnosmium0001.actionale.core.action.Action
import io.github.hnosmium0001.actionale.modConfig
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
    title: Text
) : Screen(title) {
    constructor(
        components: Array<out Action>,
        trKey: String
    ) : this(components, title = TranslatableText(trKey))

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground()
        renderRadialMenu(mouseX, mouseY)
        super.render(mouseX, mouseY, delta)
    }

    private fun renderRadialMenu(mouseX: Int, mouseY: Int) {
        val buffer = Tessellator.getInstance().buffer
        val mouse = Vector3d(mouseX.toDouble(), mouseY.toDouble(), 0.0)
        val center = Vector3d(width / 2.0, height / 2.0, 0.0)

        RenderSystem.disableTexture()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableBlend()
        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO)
        buffer.begin(GL_TRIANGLES, VertexFormats.POSITION_COLOR)

        val sides = components.size.coerceAtLeast(modConfig.radialMenuMinSides)
        val radius = modConfig.radialMenuRadius
        val vertexAngle = TWO_PI / sides
        val flat = sides.isEven()
        // Right = 0, counter clock-wise = positive, in radians
        var angle = if (flat) vertexAngle / 2 else 0.0
        var lastVert = Vector3d(center.x + cos(angle) * radius, center.y + sin(angle) * radius, 0.0)
        for (i in (0 until sides)) {
            angle += vertexAngle
            val vert = Vector3d(center.x + cos(angle) * radius, center.y + sin(angle) * radius, 0.0)
            val col = when {
                mouse.pointInTriangle(center, lastVert, vert) -> 0x776b00ff
                i.isEven() -> 0x771f6eff
                else -> 0x771fcfff
            }.toInt()
            buffer.vertex(center).color(col).next()
            buffer.vertex(lastVert).color(col).next()
            buffer.vertex(vert).color(col).next()

            components.getOrNull(i)?.run {
                // TODO draw component
                val itemAngle = angle - vertexAngle / 2
                val itemRadius = radius + 20
                val itemPos = Vector3d(center.x + cos(itemAngle) * itemRadius, center.y + sin(itemAngle) * itemRadius, 0.0)
                textRenderer.draw(this.name, itemPos, 0xffffffff.toInt())
            }

            lastVert = vert
        }

        Tessellator.getInstance().draw()
    }

    override fun isPauseScreen() = false
}