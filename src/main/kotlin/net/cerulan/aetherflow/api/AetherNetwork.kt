package net.cerulan.aetherflow.api

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos

class AetherNetwork internal constructor() {

    private val containedBlocks: HashSet<BlockPos> = HashSet()

    fun getPower(): AetherPower {
        TODO()
    }

    // TODO
    fun getSource() {}
    fun getSink() {}

    fun fromNBT(tag: CompoundTag) {

    }

}