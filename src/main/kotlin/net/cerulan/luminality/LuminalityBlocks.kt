package net.cerulan.luminality

import net.cerulan.luminality.block.BlockLuminalFurnace
import net.cerulan.luminality.block.BlockShimmerInducer
import net.cerulan.luminality.block.lumus.BlockLumusWellspring
import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.cerulan.luminality.block.entity.LuminalFurnace
import net.cerulan.luminality.block.entity.LumusPump
import net.cerulan.luminality.block.entity.LumusRedirector
import net.cerulan.luminality.block.entity.ShimmerInducer
import net.cerulan.luminality.block.lumus.BlockLumusRedirector
import net.minecraft.block.entity.BlockEntityType

object LuminalityBlocks {
    val shimmerInducer = BlockShimmerInducer
    val lumusPump = BlockLumusPump
    val lumusRedirector = BlockLumusRedirector
    val luminalFurnace = BlockLuminalFurnace
    val lumusWellspring = BlockLumusWellspring

    object BlockEntities {
        lateinit var shimmerInducerEntity: BlockEntityType<ShimmerInducer>
        lateinit var luminalFurnaceEntity: BlockEntityType<LuminalFurnace>
        lateinit var lumusPumpEntity: BlockEntityType<LumusPump>
        lateinit var lumusRedirectorEntity: BlockEntityType<LumusRedirector>
    }

}
