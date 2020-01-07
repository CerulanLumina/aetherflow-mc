package net.cerulan.luminality

import net.cerulan.luminality.block.BlockLuminalFurnace
import net.cerulan.luminality.block.BlockShimmerInducer
import net.cerulan.luminality.block.entity.*
import net.cerulan.luminality.block.lumus.BlockLumusWellspring
import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.cerulan.luminality.block.lumus.BlockLumusRedirector
import net.cerulan.luminality.block.lumus.BlockLumusRegulator
import net.minecraft.block.entity.BlockEntityType

object LuminalityBlocks {
    val shimmerInducer = BlockShimmerInducer
    val lumusPump = BlockLumusPump
    val lumusRedirector = BlockLumusRedirector
    val lumusRegulator = BlockLumusRegulator
    val luminalFurnace = BlockLuminalFurnace
    val lumusWellspring = BlockLumusWellspring

    object BlockEntities {
        lateinit var shimmerInducerEntity: BlockEntityType<ShimmerInducer>
        lateinit var luminalFurnaceEntity: BlockEntityType<LuminalFurnace>
        lateinit var lumusPumpEntity: BlockEntityType<LumusPump>
        lateinit var lumusRedirectorEntity: BlockEntityType<LumusRedirector>
        lateinit var lumusRegulatorEntity: BlockEntityType<LumusRegulator>
    }

}
