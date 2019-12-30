package net.cerulan.aetherflow.client

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.client.blockentityrenderer.ShimmerInducerRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry

object AetherflowModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(AetherflowBlocks.BlockEntities.SHIMMER_INDUCER_ENTITY) { dispatcher ->
            ShimmerInducerRenderer(
                dispatcher
            )
        }
    }
}