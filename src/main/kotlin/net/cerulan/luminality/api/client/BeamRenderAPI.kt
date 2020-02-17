package net.cerulan.luminality.api.client

import net.cerulan.luminality.api.client.render.DefaultBeamBERenderer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.Vec3d


@Environment(EnvType.CLIENT)
object BeamRenderAPI {

    fun <T> registerDefaultBeamRenderer(type: BlockEntityType<T>, startSupplier: (be: T) -> Vec3d?, targetSupplier: (be: T) -> Vec3d?)
            where T : BlockEntity {
        if (!FabricLoader.getInstance().isModLoaded("luminality")) return
        BlockEntityRendererRegistry.INSTANCE.register(type) {
            DefaultBeamBERenderer(it, startSupplier, targetSupplier)
        }

    }

    fun <T> registerDefaultBeamRenderer(type: BlockEntityType<T>)
            where T: BlockEntity, T: BeamRenderBE {
        registerDefaultBeamRenderer(type, {be -> be.startPos}, {be -> be.targetPos})
    }



}