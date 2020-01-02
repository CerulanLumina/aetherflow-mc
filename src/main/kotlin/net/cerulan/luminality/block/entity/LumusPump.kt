package net.cerulan.luminality.block.entity

import alexiil.mc.lib.attributes.SearchOptions
import com.google.common.collect.ImmutableList
import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.cerulan.luminality.block.lumus.BlockLumusPump
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

open class LumusPump : BlockEntity(LuminalityBlocks.BlockEntities.lumusPumpEntity), Tickable,
    BlockEntityClientSerializable {

    protected val range = 16

    private fun updateCachedRange() {
        val builder = ImmutableList.builder<BlockPos>()
        for (i in 0..range) {
            builder.add(pos.offset(direction!!.opposite, i))
        }
        cachedRanged = builder.build()
    }

    var direction: Direction? = null
        set(value) {
            field = value
            updateCachedRange()
        }

    lateinit var cachedRanged: ImmutableList<BlockPos>

    var active = false
        protected set(value) {
            field = value
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(BlockLumusPump.Props.VALID, field))
        }

    var target: BlockPos? = null

    override fun tick() {
        if (direction == null) {
            direction = cachedState.get(BlockLumusPump.Props.ATTACHED)
        }
        if (world!!.isClient) return
        val node = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, pos.offset(direction), SearchOptions.inDirection(direction!!.opposite))
        if (active && node != null && target != null) {
            val targetNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, target, SearchOptions.inDirection(direction!!.opposite))
            if (targetNode != null && targetNode.mode == LumusNodeMode.SINK) {
                targetNode.flow = node.flow
                targetNode.radiance = node.radiance
            } else {
                target = null
                active = false
                sync()
            }
        } else if (node != null && target == null) {
            for (searchPos in cachedRanged) {
                val searchNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, searchPos, SearchOptions.inDirection(direction!!.opposite))
                if (searchNode != null && searchNode.mode == LumusNodeMode.SINK) {
                    target = searchPos
                    active = true
                    sync()
                }
            }
        } else {
            if (active) active = false
            if (target != null)  {
                unsetTarget()
            }
        }
    }

    fun unsetTarget() {
        if (target != null)  {
            val targetNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, target, SearchOptions.inDirection(direction!!.opposite))
            if (targetNode != null) {
                targetNode.radiance = 0
                targetNode.flow = 0
            }
            target = null
            sync()
        }
    }

    override fun toClientTag(p0: CompoundTag): CompoundTag {
        if (target != null)
            p0.putInt("range", target!!.getManhattanDistance(pos))
        return p0
    }

    override fun fromClientTag(p0: CompoundTag) {
        rangeActual = p0.getInt("range")
    }

    var rangeActual = 0

}