package net.cerulan.aetherflow.api

import com.google.common.collect.ImmutableSet
import net.cerulan.aetherflow.AetherflowUtil
import net.cerulan.aetherflow.api.attr.AetherConduit
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object AetherNetworks {

    private val worldNetworks: HashMap<Int, AetherNetworksWorld> = HashMap()

    private class AetherNetworksWorld(val world: World) {

        private val networks: HashMap<BlockPos, AetherNetwork> = HashMap()

        fun addConduitToNetwork(pos: BlockPos) {
            if (AetherAttributes.AETHER_CONDUIT.getFirstOrNull(world, pos) !is AetherConduit) throw IllegalArgumentException("Block at $pos is not a conduit")
            if (networks.containsKey(pos)) throw IllegalArgumentException("Block at $pos was already present in network")
            val sides = AetherflowUtil.getSurroundingPos(pos)
            var found = false
            for (side in sides) {
                if (networks.containsKey(side)) {
                    networks[pos] = networks[side]!!
                    found = true
                    break
                }
            }
            if (!found) {
                networks[pos] = AetherNetwork(world)
            }
            val thisNet = networks[pos]!!
            thisNet.addConduitToNetwork(pos)


            for (side in sides) {
                val nodeAttr = AetherAttributes.AETHER_NODE.getFirstOrNull(world, side)
                if (nodeAttr != null) {
                    if ((thisNet.source.pos == null && nodeAttr.mode == AetherNodeMode.SOURCE) || (thisNet.sink.pos == null && nodeAttr.mode == AetherNodeMode.SINK)) {
                        thisNet.addNodeToNetwork(side)
                    }
                }
            }

        }

        /**
         * Attempts to add a node to the network. Returns false if the network already has a node of the same type.
         */
        fun addNodeToNetworks(pos: BlockPos): ImmutableSet<BlockPos> {
            if (AetherAttributes.AETHER_NODE.getFirstOrNull(world, pos) !is AetherNode) throw IllegalArgumentException("Block at $pos is not a node")
            val sides = AetherflowUtil.getSurroundingPos(pos)
            val visited: HashSet<AetherNetwork> = HashSet()
            val attached = HashSet<BlockPos>()
            for (i in sides.indices) {
                val side = sides[i]
                if (networks.containsKey(side)) {
                    if (!visited.contains(networks[side])) {
                        if (networks[side]!!.addNodeToNetwork(pos)) {
                            attached.add(side)
                        }
                        visited.add(networks[side]!!)
                    }
                }
            }
            return ImmutableSet.copyOf(attached)
        }

        fun removeBlockFromNetwork(pos: BlockPos) {
            if (!networks.containsKey(pos)) throw IllegalArgumentException("Block $pos is not in network")
            networks[pos]!!.removeBlockFromNetwork(pos)
            networks.remove(pos)
        }

        fun fromNBT(tag: CompoundTag) {
            networks.clear()
            tag.getList("networks", 10).stream().map { t -> t as CompoundTag }
                .forEach { netTag -> run {
                    val network = AetherNetwork(world)
                    network.fromNBT(tag)
                    network.getContainedBlockPos().forEach { cpos -> assert(networks.put(cpos, network) == null) {"Contained block at $cpos was in multiple networks"} }
                } }
        }

        fun toNBT(tag: CompoundTag): CompoundTag {
            val list = ListTag()
            networks.values.distinct().forEach {network -> list.add(network.toNBT(CompoundTag())) }
            tag.put("networks", list)
            return tag
        }

        fun getNetworkForBlock(pos: BlockPos) = networks[pos]

        fun tick() {
            networks.values.distinct().forEach(AetherNetwork::tick)
        }
    }

    internal fun tick(world: World) {
        val id = world.dimension.type.rawId
        if (worldNetworks.containsKey(id)) worldNetworks[id]!!.tick()
    }

    fun fromNBT(world: World, tag: CompoundTag) {
        val net = worldNetworks[world.dimension.type.rawId] ?: AetherNetworksWorld(world)
        net.fromNBT(tag)
        worldNetworks[world.dimension.type.rawId] = net
    }

    fun toNBT(world: World, tag: CompoundTag): CompoundTag {
        worldNetworks.values.forEach { wn -> wn.toNBT(tag) }
        return tag
    }

    fun getNetworkForBlock(pos: BlockPos, world: World): AetherNetwork? = worldNetworks[world.dimension.type.rawId]?.getNetworkForBlock(pos)

    fun addConduitToNetwork(world: World, pos: BlockPos) {
        worldNetworks[world.dimension.type.rawId]!!.addConduitToNetwork(pos)
    }

    fun addNodeToNetwork(world: World, pos: BlockPos) {
        worldNetworks[world.dimension.type.rawId]!!.addNodeToNetworks(pos)
    }

    fun removeBlockFromNetwork(world: World, pos: BlockPos) {
        worldNetworks[world.dimension.type.rawId]!!.removeBlockFromNetwork(pos)
    }

}