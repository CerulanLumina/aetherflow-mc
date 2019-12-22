package net.cerulan.aetherflow.client.blockentityrenderer

import net.cerulan.aetherflow.blockentity.ShimmerInducer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import kotlin.math.sin

class ShimmerInducerRenderer(dispatcher: BlockEntityRenderDispatcher) : BlockEntityRenderer<ShimmerInducer>(dispatcher) {
    override fun render(
        blockEntity: ShimmerInducer,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val stack = blockEntity.getInvStack(0)
        if (stack.isEmpty) return
        matrices.push()
        val offset = sin((blockEntity.world!!.time + tickDelta) / 4) / 80.0
        matrices.translate(0.5, 0.6 + offset, 0.5)
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.world!!.time + tickDelta) * 1))
        matrices.scale(0.5f, 0.5f, 0.5f)
        val lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.world, blockEntity.pos.up())
        MinecraftClient.getInstance().itemRenderer.renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers)
        matrices.pop()
    }
}