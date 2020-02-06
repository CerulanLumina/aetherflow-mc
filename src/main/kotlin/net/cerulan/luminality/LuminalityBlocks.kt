package net.cerulan.luminality

import net.cerulan.luminality.block.BlockLuminalFurnace
import net.cerulan.luminality.block.BlockShimmerInducer
import net.cerulan.luminality.block.lumus.LumusWellspringBlock
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.entity.LuminalFurnace
import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.entity.ShimmerInducer
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.minecraft.block.entity.BlockEntityType

object LuminalityBlocks {
    val shimmerInducer = BlockShimmerInducer
    val lumusPump = LumusPumpBlock
    val lumusRedirector = LumusRedirectorBlock
    val luminalFurnace = BlockLuminalFurnace
    val lumusWellspring = LumusWellspringBlock

    object BlockEntities {
        lateinit var shimmerInducerEntity: BlockEntityType<ShimmerInducer>
        lateinit var luminalFurnaceEntity: BlockEntityType<LuminalFurnace>
        lateinit var lumusPumpEntity: BlockEntityType<LumusPump>
        lateinit var lumusRedirectorEntity: BlockEntityType<LumusRedirector>
    }

}
