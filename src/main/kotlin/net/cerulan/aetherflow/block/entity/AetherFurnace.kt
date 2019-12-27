package net.cerulan.aetherflow.block.entity

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.block.entity.BlockEntity

class AetherFurnace : BlockEntity(AetherflowBlocks.SHIMMER_INDUCER_ENTITY) {

    val aetherNode = AetherNode(AetherNodeMode.SINK)

}