package net.cerulan.aetherflow.block.conduit

import net.cerulan.aetherflow.block.BlockAetherFurnace
import net.cerulan.aetherflow.block.entity.AetherPump
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

object BlockAetherPump :
    Block(
        FabricBlockSettings.of(Material.STONE).breakByHand(true).strength(
            0.5f,
            10f
        ).sounds(BlockSoundGroup.METAL).build()
    ),
    BlockEntityProvider {

    object Props {
        val ATTACHED = DirectionProperty.of("attached") {true}!!
        val VALID = BooleanProperty.of("valid")
    }

    init {
        defaultState = stateManager.defaultState.with(Props.ATTACHED, Direction.DOWN).with(Props.VALID, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.ATTACHED, Props.VALID)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(BlockAetherFurnace.Props.FACING, ctx.playerLookDirection)

    override fun createBlockEntity(view: BlockView) = AetherPump()

}