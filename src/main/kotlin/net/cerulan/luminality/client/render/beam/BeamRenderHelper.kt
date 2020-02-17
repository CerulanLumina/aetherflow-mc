package net.cerulan.luminality.client.render.beam

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Rotation3
import net.minecraft.client.util.math.Rotation3Helper
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3d
import kotlin.math.PI
import kotlin.math.sqrt

object BeamRenderHelper {

    private val color = floatArrayOf(0f, 0.8f, 1f, 1f)
//    private val matrixStack = MatrixStack()

    fun renderLumusBeam(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        from: Vec3d,
        toas: Vec3d,
        time: Long,
        tickDelta: Float
    ) {
        matrixStack.push()

        val to = Vec3d(toas.x + 3, toas.y + 3, toas.z)

        matrixStack.translate(0.5, 0.5 , 0.5)
        val a = from.crossProduct(to)
        val w = sqrt(from.lengthSquared() * to.lengthSquared() + from.dotProduct(to)).toFloat()
        val fromf = Vector3f(from.x.toFloat(), from.y.toFloat(), from.z.toFloat())
        val tof = Vector3f(to.x.toFloat(), to.y.toFloat(), to.z.toFloat())
        tof.subtract(fromf)

        val x = PI.toFloat() / 2 - MathHelper.atan2(tof.y.toDouble(), tof.z.toDouble()).toFloat()
        val y = MathHelper.atan2(tof.x.toDouble(), tof.z.toDouble()).toFloat()
        val z = MathHelper.atan2(tof.x.toDouble(), tof.y.toDouble()).toFloat()


        val rotation = Quaternion(x, 0f, -y, false)
//        matrixStack.multiply(rotation)


        val range = from.distanceTo(to)


        BeamRenderer.renderLumusBeamPostTransform(
            matrixStack,
            vertexConsumerProvider,
            tickDelta,
            time,
            range.toFloat(),
            color
        )
        matrixStack.pop()
    }
}