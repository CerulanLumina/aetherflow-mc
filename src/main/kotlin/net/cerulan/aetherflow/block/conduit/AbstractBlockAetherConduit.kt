package net.cerulan.aetherflow.block.conduit

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.aetherflow.api.attr.AetherConduit
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractBlockAetherConduit(val conduit: AetherConduit) :
    Block(FabricBlockSettings.of(Material.STONE).breakByHand(true).strength(0.2f, 10f).sounds(BlockSoundGroup.GLASS).build()),
    AttributeProvider {

    object Props {
        val NORTH = BooleanProperty.of("north")
        val SOUTH = BooleanProperty.of("south")
        val EAST = BooleanProperty.of("east")
        val WEST = BooleanProperty.of("west")
        val UP = BooleanProperty.of("up")
        val DOWN = BooleanProperty.of("down")
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.NORTH, Props.SOUTH, Props.EAST, Props.WEST, Props.UP, Props.DOWN)
    }

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, attributeList: AttributeList<*>) {
        attributeList.offer(conduit)
    }

}