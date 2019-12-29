package net.cerulan.aetherflow.api

import alexiil.mc.lib.attributes.SearchOptions
import com.google.common.collect.ImmutableSet
import net.cerulan.aetherflow.AetherflowUtil
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object AetherNetworks {

    private val worldNetworks: HashMap<Int, AetherNetworksWorld> = HashMap()

    private class AetherNetworksWorld(val world: World) {

        private val networks: HashMap<BlockPos, AetherNetwork> = HashMap()

        fun addConduitToNetwork(pos: BlockPos) {
            val conduit = AetherAttributes.AETHER_CONDUIT.getFirstOrNull(world, pos)
                ?: throw IllegalArgumentException("Block at $pos is not a conduit")
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
                val nodeAttr = AetherAttributes.AETHER_NODE.getFirstOrNull(world, side, SearchOptions.inDirection(AetherflowUtil.getDirectionPos(side, pos)!!))
                if (nodeAttr != null) {
                    if ((thisNet.source.pos == null && nodeAttr.mode == AetherNodeMode.SOURCE) || (thisNet.sink.pos == null && nodeAttr.mode == AetherNodeMode.SINK)) {
                        thisNet.addNodeToNetwork(side, AetherflowUtil.getDirectionPos(side, pos)!!)
                    }
                }
            }

        }

        fun addNodeToNetworks(pos: BlockPos): ImmutableSet<BlockPos> {
            val visited: HashSet<AetherNetwork> = HashSet()
            val attached: HashSet<BlockPos> = HashSet()
            for (direction in Direction.values()) {
                val node = AetherAttributes.AETHER_NODE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction))
                if (node != null) {
                    val netpos = pos.offset(direction)
                    if (networks.containsKey(netpos) && !visited.contains(networks[netpos])) {
                        val net = networks[netpos]!!
                        visited.add(net)
                        net.addNodeToNetwork(pos, direction)
                        attached.add(netpos)
                    }
                }
            }
            return ImmutableSet.copyOf(attached)
        }

        fun removeBlockFromNetwork(pos: BlockPos) {
            if (!networks.containsKey(pos)) {
                networks.values.distinct().filter { an -> an.source.pos == pos || an.sink.pos == pos }.forEach { an -> an.removeBlockFromNetwork(pos) }
                return
            }
            networks[pos]!!.removeBlockFromNetwork(pos)
            val net = networks.remove(pos)!!
            val blockSet = net.getContainedBlockPos()
            val managed: HashSet<BlockPos> = HashSet()
            val predicate = {bp: BlockPos -> !managed.contains(bp) && (net.source.pos == bp || net.sink.pos == bp || blockSet.contains(bp)) }
            AetherflowUtil.getSurroundingPos(pos)
                .forEach { start -> run {
                    if (predicate(start)) {
                        val found = AetherflowUtil.blockPosDFS(start, predicate)
                        if (found.size > 0) {
                            val new = AetherNetwork(world)
                            val partition = found.partition(blockSet::contains)
                            partition.second.forEach { bp -> run {
                                net.removeBlockFromNetwork(bp)
                                managed.add(bp)
                            }}
                            partition.first.forEach { bp -> run {
                                net.removeBlockFromNetwork(bp)
                                networks.remove(bp)
                                addConduitToNetwork(bp)
                                managed.add(bp)
                            } }

                        }
                    }
                } }

        }

        fun fromNBT(tag: CompoundTag) {
            networks.clear()
            tag.getList("networks", 10).stream().map { t -> t as CompoundTag }
                .forEach { netTag -> run {
                    val network = AetherNetwork(world)
                    network.fromNBT(netTag)
                    network.getContainedBlockPos().forEach { cpos -> assert(networks.put(cpos, network) == null) {"Contained block at $cpos was in multiple networks"} }
                } }
        }

        fun toNBT(tag: CompoundTag): CompoundTag {
            val list = ListTag()
            networks.values.distinct().forEach {network -> list.add(network.toNBT(CompoundTag())) }
            tag.put("networks", list)
            return tag
        }

        fun getNetworkForConduit(pos: BlockPos) = networks[pos]

        fun tick() {
            networks.values.distinct().forEach(AetherNetwork::tick)
        }

        val networkCount: Int
            get() = networks.values.distinct().count()

    }

    internal fun tick(world: World) {
        val id = world.dimension.type.rawId
        if (worldNetworks.containsKey(id)) worldNetworks[id]!!.tick()
    }

    fun fromNBT(world: World, tag: CompoundTag) {
        val net = AetherNetworksWorld(world)
        net.fromNBT(tag)
        worldNetworks[world.dimension.type.rawId] = net
    }

    fun toNBT(world: World, tag: CompoundTag): CompoundTag {
        return getWorldNetwork(world).toNBT(tag)
    }

    private fun getWorldNetwork(world: World): AetherNetworksWorld {
        return worldNetworks.computeIfAbsent(world.dimension.type.rawId) { AetherNetworksWorld(world) }
    }

    fun getNetworkForConduit(pos: BlockPos, world: World): AetherNetwork? = worldNetworks[world.dimension.type.rawId]?.getNetworkForConduit(pos)

    fun addConduitToNetwork(world: World, pos: BlockPos) {
        getWorldNetwork(world).addConduitToNetwork(pos)
    }

    fun addNodeToNetwork(world: World, pos: BlockPos) {
        getWorldNetwork(world).addNodeToNetworks(pos)
    }

    fun removeBlockFromNetwork(world: World, pos: BlockPos) {
        getWorldNetwork(world).removeBlockFromNetwork(pos)
    }

    val trackedWorldCount
        get() = worldNetworks.size

    fun getNetworkCountInWorld(world: World) = worldNetworks[world.dimension.type.rawId]?.networkCount ?: 0

    val networkCountAllWorlds
        get() = worldNetworks.values.sumBy { anw -> anw.networkCount }

}