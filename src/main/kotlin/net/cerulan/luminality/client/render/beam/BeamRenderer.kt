package net.cerulan.luminality.client.render.beam

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.Matrix3f
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.sqrt

object BeamRenderer {
    private val defaultBeamTex = Identifier("luminality:textures/item/shimmering_ingot.png")

    fun renderLumusBeamPostTransform(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        texture: Identifier?,
        targetPosOffset: Vector3f,
        tickDelta: Float,
        time: Long,
        color: FloatArray,
        size: Float
    ) {
        renderLightBeam(
            matrixStack,
            vertexConsumerProvider,
            texture ?: defaultBeamTex,
            targetPosOffset,
            tickDelta,
            time,
            color,
            size
        )
    }

    private fun renderLightBeam(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        identifier: Identifier,
        targetPosOffset: Vector3f,
        // unused, future plans?
        tickDelta: Float,
        time: Long,
        color: FloatArray,
        size: Float
    ) {

        matrixStack.push()
        val colorR = color[0]
        val colorG = color[1]
        val colorB = color[2]

        val length = targetPosOffset.length()

        val yAngle = MathHelper.atan2(-targetPosOffset.z.toDouble(), targetPosOffset.x.toDouble()).toFloat()
        val baseDist = MathHelper.sqrt(targetPosOffset.x * targetPosOffset.x + targetPosOffset.z * targetPosOffset.z)
        val zAngle = MathHelper.atan2(targetPosOffset.y.toDouble(), baseDist.toDouble()).toFloat()

        matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(yAngle))
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(zAngle))
        renderBeam(
            matrixStack,
            // TODO - use a better render layer (custom?)
            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(identifier, true)),
            colorR,
            colorG,
            colorB,
            color[3],
            length,
            size
        )
        matrixStack.pop()
    }

    private fun Vector3f.length(): Float = sqrt(this.x * this.x + this.y * this.y + this.z * this.z)

    private fun renderBeam(
        matrixStack: MatrixStack,
        vertexConsumer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        length: Float,
        size: Float
    ) {
        val entry = matrixStack.peek()
        val matrix4f = entry.model
        val matrix3f = entry.normal

        val lowBound = -size / 2f
        val highBound = size / 2f
        val texRepeat = 4 * length

        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, highBound, highBound, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, lowBound, highBound, 0f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, lowBound, highBound, texRepeat, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, highBound, highBound, texRepeat, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, lowBound, lowBound, texRepeat, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, highBound, lowBound, texRepeat, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, highBound, lowBound, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, lowBound, lowBound, 0f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, highBound, lowBound, texRepeat, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, highBound, highBound, texRepeat, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, highBound, highBound, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, highBound, lowBound, 0f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, lowBound, lowBound, texRepeat, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, length, lowBound, highBound, texRepeat, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, lowBound, highBound, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0f, lowBound, lowBound, 0f, 1f)
    }

    private fun vertex(
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        vertexConsumer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float
    ) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(red, green, blue, alpha).texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
    }

}