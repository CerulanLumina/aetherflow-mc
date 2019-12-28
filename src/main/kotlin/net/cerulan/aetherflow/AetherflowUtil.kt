package net.cerulan.aetherflow

import net.minecraft.util.math.BlockPos

object AetherflowUtil {

    fun getSurroundingPos(pos: BlockPos): Array<BlockPos> {
        return Array(6) { i -> when (i) {
            0 -> pos.down()
            1 -> pos.up()
            2 -> pos.north()
            3 -> pos.south()
            4 -> pos.west()
            5 -> pos.east()
            else -> pos
        } }
    }



}