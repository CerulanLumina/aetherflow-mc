package net.cerulan.luminality.client.render.beam

import net.cerulan.luminality.api.client.BeamRenderAPI
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry

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
            val toPos = BlockPos(to.x.toInt(), to.y.toInt(), to.z.toInt())
            val toBe = blockEntity.world!!.getBlockEntity(toPos)
            val toBETypeId = Registry.BLOCK_ENTITY_TYPE.getId(toBe?.type)
            val toFin = if (toBETypeId != null && BeamRenderAPI.receiverOverrideMap.containsKey(toBETypeId)) {
                BeamRenderAPI.receiverOverrideMap[toBETypeId]!!(toBe!!) ?: return
            } else {
                to.add(0.5, 0.5, 0.5)
            }

            matrices.push()
            matrices.translate(-blockEntity.pos.x.toDouble(), -blockEntity.pos.y.toDouble(), -blockEntity.pos.z.toDouble())
            BeamRenderHelper.renderLumusBeamAbsolute(
                matrices,
                vertexConsumers,
                from,
                toFin,
                blockEntity.world!!.time,
                tickDelta
            )

            matrices.pop()
        }
    }

    override fun rendersOutsideBoundingBox(blockEntity: T) = true

}