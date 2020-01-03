package net.cerulan.luminality.client

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import kotlin.math.sin

object LumusRenderers {

    private val color = floatArrayOf(0f, 0.8f, 1f, 1f)
    private fun getColor(time: Float, trans: Boolean = false): FloatArray {
        val red = 0f
        val green = (sin(time.toDouble() / 20f ) * 0.25 + 0.75).toFloat()
        val blue = 1f
        val alpha = if (trans) 0.6f else 1f
        color[0] = red
        color[1] = green
        color[2] = blue
        color[3] = alpha
        return color
    }

    fun renderLumusBeam(from: Vector3f, to: Vector3f, time: Long, tickDelta: Float) {
        val color = getColor(time + tickDelta)
        TODO()
    }

    fun lineRender(matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, color: FloatArray, pos: Vector3f, to: Vector3f) {
        val vtx = vertexConsumerProvider.getBuffer(RenderLayer.getLines())
        val m = matrixStack.peek().model
        vtx.vertex(m, pos.x, pos.y, pos.z).color(color[0], color[1], color[2], color[3]).next()
        vtx.vertex(m,  to.x, to.y, to.z).color(color[0], color[1], color[2], color[3]).next()
    }

    fun renderLumusBeam(
        range: Float,
        color: FloatArray?,
        beamDirection: Direction,
        tickDelta: Float,
        worldTime: Long,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider
    ) {
        when (beamDirection.opposite!!) {
            Direction.NORTH -> {
                matrixStack.push()
                matrixStack.translate(0.0, 1.0, 0.5)
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f))
                matrixStack.push()
            }
            Direction.SOUTH -> {
                matrixStack.push()
                matrixStack.translate(0.0, 0.0, 0.5)
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(270f))
                matrixStack.push()
            }
            Direction.WEST -> {
                matrixStack.push()
                matrixStack.translate(0.5, 0.0, 0.0)
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270f))
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(270f))
            }
            Direction.EAST -> {
                matrixStack.push()
                matrixStack.translate(0.5, 1.0, 0.0)
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270f))
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f))
            }
            Direction.UP -> {
                matrixStack.push()
                matrixStack.translate(0.0, 0.5, 1.0)
                matrixStack.push()
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180f))
                matrixStack.push()

            }
            Direction.DOWN -> {
                matrixStack.push()
                matrixStack.translate(0.0, 0.5, 0.0)
                matrixStack.push()
                matrixStack.push()
            }
        }

        renderLumusBeamPostTransform(
            matrixStack,
            vertexConsumerProvider,
            tickDelta,
            worldTime,
            range,
            color ?: getColor(worldTime + tickDelta, true)
        )
        matrixStack.pop()
        matrixStack.pop()
        matrixStack.pop()
    }

    private fun renderLumusBeamPostTransform(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        tickDelta: Float,
        time: Long,
        range: Float,
        color: FloatArray
    ) {
        renderLightBeam(
            matrixStack,
            vertexConsumerProvider,
            BeaconBlockEntityRenderer.BEAM_TEX,
            tickDelta,
            1.0f,
            time,
            range,
            color,
            0.2f
        )
    }

    private fun renderLightBeam(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        identifier: Identifier?,
        tickDelta: Float,
        g: Float,
        time: Long,
        range: Float,
        color: FloatArray,
        h: Float
    ) {
        matrixStack.push()
        matrixStack.translate(0.5, 0.0, 0.5)
        val n = Math.floorMod(time, 40L).toFloat() + tickDelta
        val o = if (range < 0) n else -n
        val p = MathHelper.fractionalPart(o * 0.2f - MathHelper.floor(o * 0.1f).toFloat())
        val colorR = color[0]
        val colorG = color[1]
        val colorB = color[2]
        matrixStack.push()
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n * 2.25f - 45.0f))
        val aj = -h
        val aa = -h
        val ap = -1.0f + p
        val aq = range * g * (0.5f / h) + ap
        renderBeam(
            matrixStack,
            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(identifier, true)),
            colorR,
            colorG,
            colorB,
            color[3],
            0f,
            range,
            0.0f,
            h,
            h,
            0.0f,
            aj,
            0.0f,
            0.0f,
            aa,
            0.0f,
            1.0f,
            aq,
            ap
        )
        matrixStack.pop()
        matrixStack.pop()
    }

    private fun renderBeam(
        matrixStack: MatrixStack,
        vertexConsumer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        y2: Float,
        y: Float,
        x2: Float,
        z2: Float,
        n: Float,
        o: Float,
        x: Float,
        z: Float,
        r: Float,
        s: Float,
        u2: Float,
        u: Float,
        v: Float,
        v2: Float
    ) {
        val entry = matrixStack.peek()
        val matrix4f = entry.model
        val matrix3f = entry.normal
        renderSide(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, y, x2, z2, n, o, u2, u, v, v2)
        renderSide(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, y, r, s, x, z, u2, u, v, v2)
        renderSide(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, y, n, o, r, s, u2, u, v, v2)
        renderSide(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, y, x, z, x2, z2, u2, u, v, v2)
    }

    private fun renderSide(
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        vertexConsumer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        y2: Float,
        y: Float,
        x: Float,
        z: Float,
        x2: Float,
        z2: Float,
        u2: Float,
        u: Float,
        v: Float,
        v2: Float
    ) {
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y, x, z, u, v)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, x, z, u, v2)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y2, x2, z2, u2, v2)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, y, x2, z2, u2, v)
    }

    private fun vertex(
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        vertexConsumer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        y: Float,
        x: Float,
        z: Float,
        u: Float,
        v: Float
    ) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(red, green, blue, alpha).texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
    }

}