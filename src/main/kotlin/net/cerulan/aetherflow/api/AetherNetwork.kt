package net.cerulan.aetherflow.api

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

    private fun updateNodeFromPos(blockPos: BlockPos?): AetherNode? {
        return if (blockPos is BlockPos) {
            if (world.isChunkLoaded(blockPos.x.shr(4), blockPos.z.shr(4))) {
                val srcNode = AetherAttributes.AETHER_NODE.getFirstOrNull(world, blockPos)
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

        source.node = updateNodeFromPos(source.pos)
        sink.node = updateNodeFromPos(sink.pos)

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

    internal fun addNodeToNetwork(pos: BlockPos): Boolean {
        val node = AetherAttributes.AETHER_NODE.getFirstOrNull(world, pos)
            ?: throw IllegalArgumentException("Attempted to add node block at $pos to network but it did not have node attribute")

        if (node.mode == AetherNodeMode.SINK) {
            if (sink.node != null) return false
            sink.node = node
            sink.pos = BlockPos(pos)
        } else {
            if (source.node != null) return false
            source.node = node
            source.pos = BlockPos(pos)
        }
        return true
    }

    internal fun removeBlockFromNetwork(pos: BlockPos) {
        if (containedBlocks.contains(pos)) {
            containedBlocks.remove(pos)
        } else {
            if (source.pos is BlockPos && source.pos!! == pos) {
                source.pos = null
                source.node = null
            } else if (sink.pos is BlockPos && sink.pos!! == pos) {
                sink.pos = null
                sink.node = null
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
        if (tag.contains("source.pos")) {
            val spos = tag.getCompound("source.pos")
            val x = spos.getInt("x")
            val y = spos.getInt("y")
            val z = spos.getInt("z")
            source.pos = BlockPos(x, y, z)
        }
        if (tag.contains("sink.pos")) {
            val spos = tag.getCompound("sink.pos")
            val x = spos.getInt("x")
            val y = spos.getInt("y")
            val z = spos.getInt("z")
            sink.pos = BlockPos(x, y, z)
        }
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
        if (source.pos is BlockPos) {
            val spos = CompoundTag()
            spos.putInt("x", source.pos!!.x)
            spos.putInt("y", source.pos!!.y)
            spos.putInt("z", source.pos!!.z)
            tag.put("source.pos", spos)
        }
        if (sink.pos is BlockPos) {
            val spos = CompoundTag()
            spos.putInt("x", sink.pos!!.x)
            spos.putInt("y", sink.pos!!.y)
            spos.putInt("z", sink.pos!!.z)
            tag.put("sink.pos", spos)
        }
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
    }

    val source: ConnectedNode = ConnectedNode()
    val sink: ConnectedNode = ConnectedNode()

}