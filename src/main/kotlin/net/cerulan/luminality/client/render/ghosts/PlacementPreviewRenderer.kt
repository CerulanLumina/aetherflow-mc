package net.cerulan.luminality.client.render.ghosts

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Camera
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult

object PlacementPreviewRenderer {


    val previewLayer: RenderLayer = PlacementPreviewRenderLayer()
    private val layers = Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder>()
    private val vcps = VertexConsumerProvider.immediate(layers, buffer())

    init {
        addRenderLayer(previewLayer)
    }

    private fun buffer() = BufferBuilder(1.shl(12))

    fun addRenderLayer(layer: RenderLayer) {
        layers.putIfAbsent(layer, buffer())
    }

    private fun getStateToRender(playerEntity: PlayerEntity, hand: Hand, stack: ItemStack): BlockState? {
        val mc = MinecraftClient.getInstance()
        val hit = mc.crosshairTarget
        if (hit !is BlockHitResult || hit.type != HitResult.Type.BLOCK) {
            return null
        }
        val item = stack.item
        if (item is BlockItem) {
            return item.block.getPlacementState(ItemPlacementContext(ItemUsageContext(playerEntity, hand, hit)))
        }
        return null
    }

    fun render(matrixStack: MatrixStack, camera: Camera, playerEntity: PlayerEntity) {
        matrixStack.push()
        matrixStack.translate(-camera.pos.x, -camera.pos.y, -camera.pos.z)

        val state = getStateToRender(playerEntity, Hand.MAIN_HAND, playerEntity.mainHandStack) ?: getStateToRender(
            playerEntity,
            Hand.OFF_HAND,
            playerEntity.offHandStack
        )
        if (state != null) {
            renderInternal(matrixStack, state)
        }
        vcps.draw()

        matrixStack.pop()
    }

    private fun renderInternal(matrixStack: MatrixStack, state: BlockState) {
        val buffer = vcps.getBuffer(previewLayer)
        val mc = MinecraftClient.getInstance()
        val blockModelRenderer = mc.blockRenderManager.modelRenderer
        val hit = mc.crosshairTarget as BlockHitResult
        val pos = hit.blockPos.offset(hit.side)
        val model = mc.blockRenderManager.getModel(state)

        matrixStack.push()
        matrixStack.translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        blockModelRenderer.render(mc.world, model, state, pos, matrixStack, buffer, true, mc.world!!.random, 0, -1)
        matrixStack.pop()
    }


}