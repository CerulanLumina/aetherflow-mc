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
            if (world!!.getBlockState(pos).block == BlockLumusPump)
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
                unsetTarget()
            }
        } else if (node != null && target == null) {
            for (searchPos in cachedRanged) {
                val searchNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, searchPos, SearchOptions.inDirection(direction!!.opposite))
                if (searchNode != null && searchNode.mode == LumusNodeMode.SINK) {
                    active = true
                    target = searchPos
                    rangeActual = searchPos.getManhattanDistance(pos)
                    offset = searchNode.attachRange
                    sync()
                    break
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
            active = false
            rangeActual = 0
            offset = 0f
            sync()
        }
    }

    override fun toClientTag(p0: CompoundTag): CompoundTag {
        p0.putInt("range", rangeActual)
        p0.putFloat("offset", offset)
        return p0
    }

    override fun fromClientTag(p0: CompoundTag) {
        rangeActual = p0.getInt("range")
        offset = p0.getFloat("offset")
    }

    var rangeActual = 0
    var offset: Float = 0f

}