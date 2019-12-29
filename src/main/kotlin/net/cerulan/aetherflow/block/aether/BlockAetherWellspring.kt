package net.cerulan.aetherflow.block.aether

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockAetherWellspring: Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing().build()), AttributeProvider {

    private val source = AetherNode(AetherNodeMode.SOURCE)
        get() {
            field.radiance = 4
            field.flow = 2
            return field
        }

    override fun addAllAttributes(p0: World?, p1: BlockPos?, p2: BlockState?, p3: AttributeList<*>?) {
        p3!!.offer(source)
    }

}