package net.cerulan.aetherflow.block.entity

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.Tickable

class AetherPump : BlockEntity(AetherflowBlocks.BlockEntities.AETHER_PUMP_ENTITY), Tickable {

    val source = AetherNode(AetherNodeMode.SOURCE)

    override fun tick() {
        if (world!!.isClient) return
        world!!.getBlockState(getPos())
    }


}