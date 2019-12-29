package net.cerulan.aetherflow

import net.cerulan.aetherflow.block.BlockAetherFurnace
import net.cerulan.aetherflow.block.BlockShimmerInducer
import net.cerulan.aetherflow.block.aether.BlockAetherWellspring
import net.cerulan.aetherflow.block.aether.BlockAetherPump
import net.cerulan.aetherflow.block.entity.AetherFurnace
import net.cerulan.aetherflow.block.entity.AetherPump
import net.cerulan.aetherflow.block.entity.ShimmerInducer
import net.minecraft.block.entity.BlockEntityType

object AetherflowBlocks {
    val SHIMMER_INDUCER = BlockShimmerInducer
    val AETHER_FURNACE = BlockAetherFurnace
    val AETHER_PUMP = BlockAetherPump
    val AETHER_WELLSPRING = BlockAetherWellspring

    object BlockEntities {
        lateinit var SHIMMER_INDUCER_ENTITY: BlockEntityType<ShimmerInducer>
        lateinit var AETHER_FURNACE_ENTITY: BlockEntityType<AetherFurnace>
        lateinit var AETHER_PUMP_ENTITY: BlockEntityType<AetherPump>
    }

}
