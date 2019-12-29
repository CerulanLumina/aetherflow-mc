package net.cerulan.aetherflow.block.entity

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.minecraft.block.entity.BlockEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Tickable

class AetherFurnace : BlockEntity(AetherflowBlocks.BlockEntities.AETHER_FURNACE_ENTITY),
    Tickable {

    val aetherSink = AetherNode(AetherNodeMode.SINK)

    override fun tick() {
        if (world!!.isClient) return
        println("R: ${aetherSink.radiance}, F: ${aetherSink.flow}")
        if (aetherSink.radiance >= 4) {
            world!!.addParticle(ParticleTypes.SMOKE, pos.x.toDouble(), pos.up().y.toDouble(), pos.z.toDouble(), 0.0, 4.0, 0.0)
            if (aetherSink.flow >= 4) {
                world!!.addParticle(ParticleTypes.PORTAL, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 0.0, 4.0, 0.0)
            }
        }
    }

}