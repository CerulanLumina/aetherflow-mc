package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.cerulan.luminality.client.LumusRenderers
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack

class LumusRedirectorRenderer(dispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<LumusRedirector>(dispatcher) {

    override fun render(
        lumusRedirector: LumusRedirector,
        tickDelta: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        if (!lumusRedirector.cachedState[BlockLumusPump.Props.valid]) return
        val direction = lumusRedirector.outputDirection
        matrixStack.push()
        matrixStack.translate(0.0, 0.0, 0.0)
        LumusRenderers.renderLumusBeam(lumusRedirector.rangeActual.toFloat() - 0.5f + lumusRedirector.offset, null, direction, tickDelta, lumusRedirector.world!!.time, matrixStack, vertexConsumerProvider)
        matrixStack.pop()
    }



    override fun rendersOutsideBoundingBox(af: LumusRedirector): Boolean = true
}