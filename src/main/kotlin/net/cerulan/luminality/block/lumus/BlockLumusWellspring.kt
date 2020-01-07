package net.cerulan.luminality.block.lumus

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockLumusWellspring: Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).nonOpaque().dropsNothing().build()), AttributeProvider {

    private val source = LumusNode(LumusNodeMode.SOURCE)
        get() {
            field.radiance = 1
            field.flow = 2
            return field
        }

    override fun addAllAttributes(p0: World?, p1: BlockPos?, p2: BlockState?, p3: AttributeList<*>?) {
        p3!!.offer(source)
    }

}