package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.cerulan.luminality.client.LumusRenderers
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
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import kotlin.math.sin

class LumusPumpRenderer(dispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<LumusPump>(dispatcher) {

    override fun render(
        lumusPump: LumusPump,
        tickDelta: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        if (!lumusPump.cachedState[BlockLumusPump.Props.VALID]) return
        val direction = lumusPump.cachedState[BlockLumusPump.Props.ATTACHED].opposite

        LumusRenderers.renderLumusBeam(lumusPump.rangeActual.toFloat() - 0.5f + lumusPump.offset, null, direction, tickDelta, lumusPump.world!!.time, matrixStack, vertexConsumerProvider)

    }



    override fun rendersOutsideBoundingBox(af: LumusPump): Boolean = true
}