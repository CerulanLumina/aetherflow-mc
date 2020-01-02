package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.cerulan.luminality.block.entity.LumusPump
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.Matrix3f
import net.minecraft.client.util.math.Matrix4f
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper

class LumusPumpRenderer(dispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<LumusPump>(dispatcher) {

    companion object {
        val color = FloatArray(3) { ele ->
            when (ele) {
                0 -> 0f
                1 -> 0.8f
                2 -> 1f
                else -> 0f
            }
        }
    }

    override fun render(
        lumusPump: LumusPump,
        tickDelta: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        if (!lumusPump.cachedState[BlockLumusPump.Props.VALID]) return

        val time = lumusPump.world!!.time
        matrixStack.push()

//        for (kp in 0..1000)


        val attach = lumusPump.cachedState[BlockLumusPump.Props.ATTACHED]!!

        when (attach) {
            Direction.NORTH -> {
                matrixStack.translate(0.0, 1.0, 0.5)
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f))
            }
            Direction.SOUTH -> {
                matrixStack.translate(0.0, 0.0, 0.5)
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(270f))
            }
            Direction.WEST -> {
                matrixStack.translate(0.5, 0.0, 0.0)
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270f))
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(270f))
            }
            Direction.EAST -> {
                matrixStack.translate(0.5, 1.0, 0.0)
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270f))
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f))
            }
            Direction.UP -> {
                matrixStack.translate(0.0, 0.5, 1.0)
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180f))

            }
            Direction.DOWN -> {
                matrixStack.translate(0.0, 0.5, 0.0)

            }
        }

        render(
            matrixStack,
            vertexConsumerProvider,
            tickDelta,
            time,
            0,
            lumusPump.rangeActual,
            color
        )
        matrixStack.pop()
    }

    private fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        f: Float,
        l: Long,
        i: Int,
        j: Int,
        fs: FloatArray
    ) {
        renderLightBeam(
            matrixStack,
            vertexConsumerProvider,
            BeaconBlockEntityRenderer.BEAM_TEX,
            f,
            1.0f,
            l,
            i,
            j,
            fs,
            0.2f,
            0.25f
        )
    }

    fun renderLightBeam(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        identifier: Identifier?,
        f: Float,
        g: Float,
        l: Long,
        i: Int,
        j: Int,
        fs: FloatArray,
        h: Float,
        k: Float
    ) {
        val m = i + j
        matrixStack.push()
        matrixStack.translate(0.5, 0.0, 0.5)
        val n = Math.floorMod(l, 40L).toFloat() + f
        val o = if (j < 0) n else -n
        val p = MathHelper.fractionalPart(o * 0.2f - MathHelper.floor(o * 0.1f).toFloat())
        val q = fs[0]
        val r = fs[1]
        val s = fs[2]
        matrixStack.push()
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n * 2.25f - 45.0f))
        var af = 0.0f
        var ai = 0.0f
        var aj = -h
        val y = 0.0f
        val z = 0.0f
        val aa = -h
        var an = 0.0f
        var ao = 1.0f
        var ap = -1.0f + p
        var aq = j.toFloat() * g * (0.5f / h) + ap
        method_22741(
            matrixStack,
            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(identifier, false)),
            q,
            r,
            s,
            1.0f,
            i,
            m,
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
        af = -k
        val ag = -k
        ai = -k
        aj = -k
        an = 0.0f
        ao = 1.0f
        ap = -1.0f + p
        aq = j.toFloat() * g + ap
        method_22741(
            matrixStack,
            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(identifier, true)),
            q,
            r,
            s,
            0.125f,
            i,
            m,
            af,
            ag,
            k,
            ai,
            aj,
            k,
            k,
            k,
            0.0f,
            1.0f,
            aq,
            ap
        )
        matrixStack.pop()
    }

    private fun method_22741(
        matrixStack: MatrixStack,
        vertexConsumer: VertexConsumer,
        f: Float,
        g: Float,
        h: Float,
        i: Float,
        j: Int,
        k: Int,
        l: Float,
        m: Float,
        n: Float,
        o: Float,
        p: Float,
        q: Float,
        r: Float,
        s: Float,
        t: Float,
        u: Float,
        v: Float,
        w: Float
    ) {
        val entry = matrixStack.peek()
        val matrix4f = entry.model
        val matrix3f = entry.normal
        method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, l, m, n, o, t, u, v, w)
        method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, r, s, p, q, t, u, v, w)
        method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, n, o, r, s, t, u, v, w)
        method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, p, q, l, m, t, u, v, w)
    }

    private fun method_22740(
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        vertexConsumer: VertexConsumer,
        f: Float,
        g: Float,
        h: Float,
        i: Float,
        j: Int,
        k: Int,
        l: Float,
        m: Float,
        n: Float,
        o: Float,
        p: Float,
        q: Float,
        r: Float,
        s: Float
    ) {
        method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, k, l, m, q, r)
        method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, l, m, q, s)
        method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, n, o, p, s)
        method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, k, n, o, p, r)
    }

    private fun method_23076(
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        vertexConsumer: VertexConsumer,
        f: Float,
        g: Float,
        h: Float,
        i: Float,
        j: Int,
        k: Float,
        l: Float,
        m: Float,
        n: Float
    ) {
        vertexConsumer.vertex(matrix4f, k, j.toFloat(), l).color(f, g, h, i).texture(m, n)
            .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
    }

    override fun rendersOutsideBoundingBox(af: LumusPump): Boolean = true
}