package net.cerulan.luminality

import net.cerulan.luminality.block.LuminalFurnaceBlock
import net.cerulan.luminality.block.ShimmerInducerBlock
import net.cerulan.luminality.block.entity.*
import net.cerulan.luminality.block.lumus.LumusRegulatorBlock
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.cerulan.luminality.block.lumus.LumusWellspringBlock
import net.minecraft.block.entity.BlockEntityType

object LuminalityBlocks {
    val shimmerInducer = ShimmerInducerBlock
    val lumusPump = LumusPumpBlock
    val lumusRedirector = LumusRedirectorBlock
    val lumusRegulator = LumusRegulatorBlock
    val luminalFurnace = LuminalFurnaceBlock
    val lumusWellspring = LumusWellspringBlock

    object BlockEntities {
        lateinit var shimmerInducerEntity: BlockEntityType<ShimmerInducer>
        lateinit var luminalFurnaceEntity: BlockEntityType<LuminalFurnace>
        lateinit var lumusPumpEntity: BlockEntityType<LumusPump>
        lateinit var lumusRedirectorEntity: BlockEntityType<LumusRedirector>
        lateinit var lumusRegulatorEntity: BlockEntityType<LumusRegulator>
    }

}
