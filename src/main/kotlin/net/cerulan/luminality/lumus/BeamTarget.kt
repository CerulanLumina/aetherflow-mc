package net.cerulan.luminality.lumus

import net.cerulan.luminality.api.attr.LumusSink
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class BeamTarget {
    var world: World? = null
        private set

    var blockPos: BlockPos? = null
        set(value) {
            if (world != null && value != null) {
                field = value
            } else {
                field = null
                cachedSink = null
            }
        }
    var cachedSink: LumusSink? = null

    fun changeWorld(world: World) {
        this.world = world
        blockPos = null
        cachedSink = null
    }
}