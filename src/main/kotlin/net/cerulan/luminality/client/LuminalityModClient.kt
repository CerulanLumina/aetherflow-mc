package net.cerulan.luminality.client

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.client.BeamRenderAPI
//import net.cerulan.luminality.client.blockentityrenderer.LumusPumpRenderer
//import net.cerulan.luminality.client.blockentityrenderer.LumusRedirectorRenderer
//import net.cerulan.luminality.client.blockentityrenderer.LumusRegulatorRenderer
import net.cerulan.luminality.client.blockentityrenderer.ShimmerInducerRenderer
import net.cerulan.luminality.client.gui.LuminalFurnaceScreen
import net.cerulan.luminality.container.LuminalFurnaceController
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