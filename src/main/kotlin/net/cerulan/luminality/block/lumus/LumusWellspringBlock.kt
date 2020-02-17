package net.cerulan.luminality.block.lumus

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.cerulan.luminality.api.attr.LumusSource
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object LumusWellspringBlock: Block(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).nonOpaque().dropsNothing().build()), AttributeProvider {

    private val source = LumusSource()
        get() {
            // allows hotswapping the values
            field.power.radiance = 4
            field.power.flow = 8
            return field
        }

    override fun addAllAttributes(p0: World?, p1: BlockPos?, p2: BlockState?, p3: AttributeList<*>?) {
        p3!!.offer(source)
    }

}