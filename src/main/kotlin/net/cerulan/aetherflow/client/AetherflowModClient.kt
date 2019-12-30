package net.cerulan.aetherflow.client

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.client.blockentityrenderer.ShimmerInducerRenderer
import net.cerulan.aetherflow.client.gui.AetherFurnaceScreen
import net.cerulan.aetherflow.container.AetherFurnaceController
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.container.BlockContext
import net.minecraft.util.Identifier

object AetherflowModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(AetherflowBlocks.BlockEntities.SHIMMER_INDUCER_ENTITY) { dispatcher ->
            ShimmerInducerRenderer(
                dispatcher
            )
        }

        ScreenProviderRegistry.INSTANCE.registerFactory(
            Identifier(
                "aetherflow",
                "aether_furnace"
            )
        ) { syncId, _, player, buf ->
            AetherFurnaceScreen(
                AetherFurnaceController(
                    syncId,
                    player.inventory,
                    BlockContext.create(player.world, buf.readBlockPos())
                ), player
            )
        }
    }
}