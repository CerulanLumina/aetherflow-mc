package net.cerulan.luminality.client.blockentityrenderer

import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.lumus.LumusPumpBlock
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
        val direction = lumusRedirector.outputDirection ?: return
        if (!lumusRedirector.cachedState[LumusPumpBlock.Props.valid]) return
        matrixStack.push()
        val vec = direction.unitVector
        val scale = 0.5f - lumusRedirector.lumusSink.attachRange
        vec.scale(scale)
        matrixStack.translate(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())
        LumusRenderers.renderLumusBeam(lumusRedirector.rangeActual.toFloat() - 0.5f + lumusRedirector.offset - scale, null, direction, tickDelta, lumusRedirector.world!!.time, matrixStack, vertexConsumerProvider)
        matrixStack.pop()
    }



    override fun rendersOutsideBoundingBox(af: LumusRedirector): Boolean = true
}
