package net.cerulan.luminality.client.render.beam

import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.*

object BeamRenderer {

     internal fun renderLumusBeamPostTransform(
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
        speed: Float,
        time: Long,
        range: Float,
        color: FloatArray,
        size: Float
    ) {
        matrixStack.push()
        val texOffset = Math.floorMod(time, 40L).toFloat() + tickDelta
        val texOffsetNorm = if (range < 0) texOffset else -texOffset
        val textOffsetFract = MathHelper.fractionalPart(texOffsetNorm * 0.2f - MathHelper.floor(texOffsetNorm * 0.1f).toFloat())
        val colorR = color[0]
        val colorG = color[1]
        val colorB = color[2]
//        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(time * 2.25f - 45.0f))
        val v2 = -1.0f + textOffsetFract
        val v = range * speed * (0.5f / size) + v2
        renderBeam(
            matrixStack,
            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Identifier("luminality:textures/item/shimmering_ingot.png"), true)),
            colorR,
            colorG,
            colorB,
            color[3],
            0f,
            2f,
            0.0f,
            size,
            size,
            0.0f,
            -size,
            0.0f,
            0.0f,
            -0.2f,
            0.0f,
            1.0f,
            v,
            v2
        )
//        renderBeam(
//            matrixStack,
//            vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(identifier, true)),
//            colorR,
//            colorG,
//            colorB,
//            color[3],
//            0f,
//            range,
//            0.0f,
//            size,
//            size,
//            0.0f,
//            -size,
//            0.0f,
//            0.0f,
//            -size,
//            0.0f,
//            1.0f,
//            v,
//            v2
//        )
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
//        renderSide(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha,
//            0f,
//            0.2f,
//            0f,
//            0.2f,
//            2f,
//            0.2f,
//            8f,
//            0f,
//            0f,
//            1f
//        )
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 0f, 0.2f, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 0f, 0.2f, 0f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 2f, 0.2f, 8f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 2f, 0.2f, 8f, 0f)

        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 0f, 0f, 8f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 0f, 0f, 8f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 2f, 0f, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 2f, 0f, 0f, 1f)

        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 0f, 0f, 8f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 0f, 0.2f, 8f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 2f, 0.2f, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.2f, 2f, 0f, 0f, 1f)

        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 2f, 0f, 8f, 1f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 2f, 0.2f, 8f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 0f, 0.2f, 0f, 0f)
        vertex(matrix4f, matrix3f, vertexConsumer, red, green, blue, alpha, 0.0f, 0f, 0f, 0f, 1f)

//        renderSide(
//            matrix4f,
//            matrix3f,
//            vertexConsumer,
//            red,
//            green,
//            blue,
//            alpha,
//            y2,
//            y,
//            r,
//            s,
//            x,
//            z,
//            u2,
//            u,
//            v,
//            v2
//        )
//        renderSide(
//            matrix4f,
//            matrix3f,
//            vertexConsumer,
//            red,
//            green,
//            blue,
//            alpha,
//            y2,
//            y,
//            n,
//            o,
//            r,
//            s,
//            u2,
//            u,
//            v,
//            v2
//        )
//        renderSide(
//            matrix4f,
//            matrix3f,
//            vertexConsumer,
//            red,
//            green,
//            blue,
//            alpha,
//            y2,
//            y,
//            x,
//            z,
//            x2,
//            z2,
//            u2,
//            u,
//            v,
//            v2
//        )
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
        vertex(
            matrix4f,
            matrix3f,
            vertexConsumer,
            red,
            green,
            blue,
            alpha,
            y,
            x,
            z,
            u,
            v
        )
        vertex(
            matrix4f,
            matrix3f,
            vertexConsumer,
            red,
            green,
            blue,
            alpha,
            y2,
            x,
            z,
            u,
            v2
        )
        vertex(
            matrix4f,
            matrix3f,
            vertexConsumer,
            red,
            green,
            blue,
            alpha,
            y2,
            x2,
            z2,
            u2,
            v2
        )
        vertex(
            matrix4f,
            matrix3f,
            vertexConsumer,
            red,
            green,
            blue,
            alpha,
            y,
            x2,
            z2,
            u2,
            v
        )
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