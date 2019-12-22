package net.cerulan.aetherflow;

import net.cerulan.aetherflow.block.BlockShimmerInducer
import net.cerulan.aetherflow.blockentity.ShimmerInducer
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType

object AetherflowBlocks {
    val SHIMMER_INDUCER = BlockShimmerInducer

    lateinit var SHIMMER_INDUCER_ENTITY: BlockEntityType<ShimmerInducer>
}
