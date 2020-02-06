package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.client.LumusRenderers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack

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

        if (!lumusPump.cachedState[LumusPumpBlock.Props.valid]) return
        val direction = lumusPump.cachedState[LumusPumpBlock.Props.input].opposite
        matrixStack.push()
        matrixStack.translate(0.0, 0.0, 0.0)
        LumusRenderers.renderLumusBeam(lumusPump.rangeActual.toFloat() - 0.5f + lumusPump.offset, null, direction, tickDelta, lumusPump.world!!.time, matrixStack, vertexConsumerProvider)
        matrixStack.pop()
    }



    override fun rendersOutsideBoundingBox(af: LumusPump): Boolean = true
}