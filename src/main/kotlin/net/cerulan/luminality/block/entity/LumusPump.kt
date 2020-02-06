package net.cerulan.luminality.block.entity

import alexiil.mc.lib.attributes.SearchOptions
import com.google.common.collect.ImmutableList
import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class LumusPump(blockEntityType: BlockEntityType<*> = LuminalityBlocks.BlockEntities.lumusPumpEntity) : BlockEntity(blockEntityType), Tickable,
    BlockEntityClientSerializable {

    protected open val range = 16

    protected open fun updateCachedRange() {
        val builder = ImmutableList.builder<BlockPos>()
        for (i in 1..range) {
            builder.add(pos.offset(outputDirection, i))
        }
        cachedRanged = builder.build()
    }

    var direction: Direction? = null
        protected set(value) {
            field = value
            if (value != null)
                updateCachedRange()
        }

    lateinit var cachedRanged: ImmutableList<BlockPos>
        protected set

    open var active = false
        protected set(value) {
            field = value
            if (LuminalityAttributes.lumusPump.getFirstOrNull(world!!, pos) != null)
                world!!.setBlockState(pos, world!!.getBlockState(pos).with(LumusPumpBlock.Props.valid, field))
        }

    open var target: BlockPos? = null
        protected set

    protected open fun getInputNode(world: World, pos: BlockPos, direction: Direction): LumusNode? {
        return LuminalityAttributes.lumusNode.getFirstOrNull(world, pos.offset(direction), SearchOptions.inDirection(direction.opposite))
    }

    open val outputDirection: Direction
        get() = direction!!.opposite

    override fun tick() {
        if (direction == null) {
            direction = cachedState.get(LumusPumpBlock.Props.input)
        }
        if (world!!.isClient) return
        val node = getInputNode(world!!, pos, direction!!)
        if (active && node != null && target != null) {
            val targetNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, target, SearchOptions.inDirection(outputDirection.opposite))
            if (targetNode != null && targetNode.mode == LumusNodeMode.SINK && cachedRanged.subList(0, rangeActual - 1).all { pos -> world!!.getBlockState(pos).isAir }) {
                targetNode.flow = node.flow
                targetNode.radiance = node.radiance
            } else {
                unsetTarget()
            }
        } else if (node != null && target == null) {
            for (searchPos in cachedRanged) {
                val searchNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, searchPos, SearchOptions.inDirection(outputDirection.opposite))
                if (searchNode != null && searchNode.mode == LumusNodeMode.SINK) {
                    active = true
                    target = searchPos
                    rangeActual = searchPos.getManhattanDistance(pos)
                    offset = searchNode.attachRange
                    sync()
                    break
                } else if (!world!!.getBlockState(searchPos).isAir) break
            }
        } else {
            if (active) active = false
            if (target != null)  {
                unsetTarget()
            }
        }
    }

    open fun unsetTarget() {
        if (target != null)  {
            val targetNode = LuminalityAttributes.lumusNode.getFirstOrNull(world!!, target, SearchOptions.inDirection(outputDirection.opposite))
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

    open var rangeActual = 0
    open var offset: Float = 0f

}