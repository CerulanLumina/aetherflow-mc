package net.cerulan.luminality.client

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.client.BeamRenderAPI
import net.cerulan.luminality.api.client.PreviewRenderAPI
import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.cerulan.luminality.block.lumus.LumusRegulatorBlock
import net.cerulan.luminality.client.blockentityrenderer.ShimmerInducerRenderer
import net.cerulan.luminality.client.gui.LuminalFurnaceScreen
import net.cerulan.luminality.container.LuminalFurnaceController
import net.cerulan.luminality.toVec3d
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.container.BlockContext
import net.minecraft.util.Identifier

object LuminalityModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(LuminalityBlocks.BlockEntities.shimmerInducerEntity) { dispatcher ->
            ShimmerInducerRenderer(
                dispatcher
            )
        }

        BeamRenderAPI.registerDefaultBeamRenderer(LuminalityBlocks.BlockEntities.lumusPumpEntity)
        BeamRenderAPI.registerDefaultBeamRenderer(LuminalityBlocks.BlockEntities.lumusRedirectorEntity,
            {lr -> lr.startPos?.add(0.5, 0.5, 0.5)?.add(lr.outputDirection.vector.toVec3d()!!.multiply(0.4))}, {lr -> lr.targetPos})
        BeamRenderAPI.registerIncomingBeamPosOverride(Identifier("luminality", "lumus_redirector")) {
            if (it is LumusRedirector) {
                it.pos.toVec3d()?.add(0.5, 0.5, 0.5)?.add(it.inputDirection.vector.toVec3d()!!.multiply(0.4))
            } else null
        }

        PreviewRenderAPI.registerNeedsPreview(LumusPumpBlock)
        PreviewRenderAPI.registerNeedsPreview(LumusRedirectorBlock)
        PreviewRenderAPI.registerNeedsPreview(LumusRegulatorBlock)

        ScreenProviderRegistry.INSTANCE.registerFactory(
            Identifier(
                "luminality",
                "luminal_furnace"
            )
        ) { syncId, _, player, buf ->
            LuminalFurnaceScreen(
                LuminalFurnaceController(
                    syncId,
                    player.inventory,
                    BlockContext.create(player.world, buf.readBlockPos())
                ), player
            )
        }
    }
}