package net.cerulan.aetherflow.api

import alexiil.mc.lib.attributes.SearchOptions
import com.google.common.collect.ImmutableSet
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class AetherNetwork internal constructor(val world: World) {

    private val containedBlocks: HashSet<BlockPos> = HashSet()

    fun getContainedBlockPos(): ImmutableSet<BlockPos> = ImmutableSet.copyOf(containedBlocks)

    fun getPower(): AetherPower {
        return if (source.node is AetherNode && sink.node is AetherNode) {
            AetherPower(source.node!!.radiance, source.node!!.flow)
        } else AetherPower(0, 0)
    }

    private fun updateNodeFromPos(blockPos: BlockPos?, direction: Direction?): AetherNode? {
        return if (blockPos is BlockPos && direction != null) {
            if (world.isChunkLoaded(blockPos.x.shr(4), blockPos.z.shr(4))) {
                val srcNode = AetherAttributes.AETHER_NODE.getFirstOrNull(world, blockPos, SearchOptions.inDirection(direction))
                if (srcNode is AetherNode) {
                    srcNode
                } else {
                    throw IllegalStateException("Known node is not AetherNode")
                }
            } else {
                null
            }
        } else null
    }

    internal fun tick() {

        source.node = updateNodeFromPos(source.pos, source.direction)
        sink.node = updateNodeFromPos(sink.pos, sink.direction)

        if (source.node != null && sink.node != null) {
            sink.node!!.radiance = source.node!!.radiance
            sink.node!!.flow = source.node!!.flow
        }
    }

    internal fun addConduitToNetwork(pos: BlockPos) {
        AetherAttributes.AETHER_CONDUIT.getFirstOrNull(world, pos)
            ?: throw IllegalArgumentException("Attempted to add conduit block at $pos to network, but it did not have conduit attribute")
        if (containedBlocks.contains(pos)) throw IllegalArgumentException("Attempted to add conduit block at $pos to network, but there was already a conduit there")
        containedBlocks.add(pos)
    }

    internal fun addNodeToNetwork(pos: BlockPos, direction: Direction): Boolean {
        val node = AetherAttributes.AETHER_NODE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction))
            ?: throw IllegalArgumentException("Attempted to add node block at $pos to network but it did not have node attribute")

        if (node.mode == AetherNodeMode.SINK) {
            if (sink.node != null) return false
            sink.node = node
            sink.pos = BlockPos(pos)
            sink.direction = direction
        } else {
            if (source.node != null) return false
            source.node = node
            source.pos = BlockPos(pos)
            source.direction = direction
        }
        return true
    }

    internal fun removeBlockFromNetwork(pos: BlockPos) {
        if (containedBlocks.contains(pos)) {
            containedBlocks.remove(pos)
        } else {
            if (source.pos is BlockPos && source.pos!! == pos) {
                source.clear()
            } else if (sink.pos is BlockPos && sink.pos!! == pos) {
                sink.clear()
            } else {
                throw IllegalArgumentException("Attempted to remove block at $pos from network, but it was not in the network")
            }
        }
    }

    internal fun fromNBT(tag: CompoundTag) {
        containedBlocks.clear()
        source.clear()
        sink.clear()
        val contained = tag.getIntArray("contained")
        if (contained.size % 3 != 0) throw IllegalStateException("Contained blocks in saved AetherNetwork is corrupt")
        for (i in contained.indices step 3) {
            val x = contained[i]
            val y = contained[i + 1]
            val z = contained[i + 2]
            containedBlocks.add(BlockPos(x, y, z))
        }
        source.fromNBT(tag.getCompound("source"))
        sink.fromNBT(tag.getCompound("sink"))
    }

    internal fun toNBT(tag: CompoundTag): CompoundTag {
        val list = ArrayList<Int>()
        list.ensureCapacity(containedBlocks.size * 3)
        containedBlocks.stream().forEach { pos ->
            run {
                list.add(pos.x)
                list.add(pos.y)
                list.add(pos.z)
            }
        }
        tag.putIntArray("contained", list)
        tag.put("source", source.toNBT(CompoundTag()))
        tag.put("sink", sink.toNBT(CompoundTag()))
        return tag
    }

    class ConnectedNode {
        var node: AetherNode? = null
            internal set
        var pos: BlockPos? = null
            internal set
        var direction: Direction? = null
            internal set

        fun clear() {
            node = null
            pos = null
            direction = null
        }

        fun toNBT(tag: CompoundTag): CompoundTag {
            if (pos != null) {
                tag.putInt("x", pos!!.x)
                tag.putInt("y", pos!!.y)
                tag.putInt("z", pos!!.z)
                tag.putInt("dir", direction!!.id)
            }
            return tag
        }

        fun fromNBT(tag: CompoundTag) {
            clear()
            if (tag.contains("x")) {
                val x = tag.getInt("x")
                val y = tag.getInt("y")
                val z = tag.getInt("z")
                val dir = tag.getInt("dir")

                pos = BlockPos(x, y, z)
                direction = Direction.byId(dir)
            }
        }

    }

    val source: ConnectedNode = ConnectedNode()
    val sink: ConnectedNode = ConnectedNode()

}