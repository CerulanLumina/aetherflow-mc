package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.entity.LumusRegulator
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.client.LumusRenderers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack

class LumusRegulatorRenderer(dispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<LumusRegulator>(dispatcher) {

    override fun render(
        lumusRegulator: LumusRegulator,
        tickDelta: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val direction = lumusRegulator.outputDirection ?: return
        if (!lumusRegulator.cachedState[LumusPumpBlock.Props.valid]) return
        matrixStack.push()
        val vec = direction.unitVector
        val scale = 0.5f - lumusRegulator.lumusSink.attachRange
        vec.scale(scale)
        matrixStack.translate(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
        LumusRenderers.renderLumusBeam(lumusRegulator.rangeActual.toFloat() - 0.5f + lumusRegulator.offset - scale, null, direction, tickDelta, lumusRegulator.world!!.time, matrixStack, vertexConsumerProvider)
        matrixStack.pop()
    }



    override fun rendersOutsideBoundingBox(af: LumusRegulator): Boolean = true
}