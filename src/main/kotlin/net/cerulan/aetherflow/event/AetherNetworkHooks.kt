package net.cerulan.aetherflow.event

import net.cerulan.aetherflow.api.AetherNetworks
import net.fabricmc.fabric.api.event.world.WorldTickCallback
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object AetherNetworkHooks {

    fun register() {
        WorldTickCallback.EVENT.register(tickNetworks)
    }

    private val tickNetworks = WorldTickCallback { world: World ->
        run {
            if (!world.isClient) AetherNetworks.tick(world)
        }
    }

    fun addNode(world: World, pos: BlockPos) {
        AetherNetworks.addNodeToNetwork(world, pos)
    }

    fun addConduit(world: World, pos: BlockPos) {
        AetherNetworks.addConduitToNetwork(world, pos)
    }

}