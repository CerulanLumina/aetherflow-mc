package net.cerulan.luminality.block.entity

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LumusRatioChanges
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class LumusRegulator : LumusPump(LuminalityBlocks.BlockEntities.lumusRegulatorEntity) {

    val lumusSink = LumusNode(LumusNodeMode.SINK)
    val lumusSource = LumusNode(LumusNodeMode.SOURCE)

    var mode = LumusRatioChanges.INCREASE_RADIANCE

    init {
        lumusSink.attachRange = 0.1f
    }

    override fun tick() {
        lumusSource.radiance = mode.getNewRadiance(lumusSink.radiance)
        lumusSource.flow = mode.getNewFlow(lumusSink.flow)
        super.tick()
    }

    override fun getInputNode(world: World, pos: BlockPos, direction: Direction): LumusNode? {
        return if (lumusSource.radiance * lumusSource.flow > 0) lumusSource else null
    }

    override fun fromTag(tag: CompoundTag) {
        mode = LumusRatioChanges.values()[tag.getInt("mode")]
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putInt("mode", mode.ordinal)
        return super.toTag(tag)
    }

}