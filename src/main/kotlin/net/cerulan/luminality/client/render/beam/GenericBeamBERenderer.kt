package net.cerulan.luminality.client.render.beam

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d

open class GenericBeamBERenderer<T>(dispatcher: BlockEntityRenderDispatcher, private val startSupplier: (T) -> Vec3d?, private val targetSupplier: (T) -> Vec3d?) : BlockEntityRenderer<T>(dispatcher)
        where T: BlockEntity {
    override fun render(
        blockEntity: T?,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {

        if (blockEntity != null && blockEntity.world != null) {
            val from = startSupplier(blockEntity) ?: return
            val to = targetSupplier(blockEntity) ?: return
            matrices.push()

            BeamRenderHelper.renderLumusBeam(
                matrices,
                vertexConsumers,
                from,
                to,
                blockEntity.world!!.time,
                tickDelta
            )

            matrices.pop()
        }
    }

    override fun rendersOutsideBoundingBox(blockEntity: T) = true

}