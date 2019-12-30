package net.cerulan.aetherflow.block.entity

import alexiil.mc.lib.attributes.SearchOptions
import com.google.common.collect.ImmutableList
import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.AetherAttributes
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.cerulan.aetherflow.block.aether.BlockAetherPump
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class AetherPump : BlockEntity(AetherflowBlocks.BlockEntities.AETHER_PUMP_ENTITY), Tickable {

    protected val range = 16

    override fun setWorld(_world: World, _blockPos: BlockPos) {
        super.setWorld(_world, _blockPos)
        direction = world!!.getBlockState(pos).get(BlockAetherPump.Props.ATTACHED)
    }

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
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(BlockAetherPump.Props.ATTACHED, value))
            updateCachedRange()
        }

    lateinit var cachedRanged: ImmutableList<BlockPos>

    var active = false
        protected set(value) {
            field = value
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(BlockAetherPump.Props.VALID, field))
        }

    var target: BlockPos? = null

    override fun tick() {
        if (world!!.isClient) return
        val node = AetherAttributes.AETHER_NODE.getFirstOrNull(world!!, pos.offset(direction), SearchOptions.inDirection(direction!!.opposite))
        if (active && node != null && target != null) {
            val targetNode = AetherAttributes.AETHER_NODE.getFirstOrNull(world!!, target, SearchOptions.inDirection(direction!!.opposite))
            if (targetNode != null && targetNode.mode == AetherNodeMode.SINK) {
                targetNode.flow = node.flow
                targetNode.radiance = node.radiance
            } else {
                target = null
                active = false
            }
        } else if (node != null && target == null) {
            for (searchPos in cachedRanged) {
                val searchNode = AetherAttributes.AETHER_NODE.getFirstOrNull(world!!, searchPos, SearchOptions.inDirection(direction!!.opposite))
                if (searchNode != null && searchNode.mode == AetherNodeMode.SINK) {
                    target = searchPos
                    active = true
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
            val targetNode = AetherAttributes.AETHER_NODE.getFirstOrNull(world!!, target, SearchOptions.inDirection(direction!!.opposite))
            if (targetNode != null) {
                targetNode.radiance = 0
                targetNode.flow = 0
            }
            target = null
        }
    }

}