package net.cerulan.luminality.client.render.beam

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.Vec3d

object BeamRenderHelper {

    private val color = floatArrayOf(0f, 0.8f, 1f, 1f)

    fun renderLumusBeamAbsolute(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        from: Vec3d,
        to: Vec3d,
        time: Long,
        tickDelta: Float
    ) {
        matrixStack.push()
        matrixStack.translate(from.x, from.y , from.z)
        val offset = to.subtract(from)
        BeamRenderer.renderLumusBeamPostTransform(
            matrixStack,
            vertexConsumerProvider,
            null,
            Vector3f(offset.x.toFloat(), offset.y.toFloat(), offset.z.toFloat()),
            tickDelta,
            time,
            color,
            0.2f
        )
        matrixStack.pop()
    }

}