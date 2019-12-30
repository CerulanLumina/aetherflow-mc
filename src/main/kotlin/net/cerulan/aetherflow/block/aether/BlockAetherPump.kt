package net.cerulan.aetherflow.block.aether

import net.cerulan.aetherflow.block.entity.AetherPump
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.EntityContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockAetherPump :
    Block(
        FabricBlockSettings.of(Material.GLASS).nonOpaque().breakByHand(true).strength(
            0.5f,
            10f
        ).sounds(BlockSoundGroup.METAL).build()
    ),
    BlockEntityProvider {

    object Props {
        val ATTACHED = DirectionProperty.of("attached") {true}!!
        val VALID = BooleanProperty.of("valid")!!
    }

    init {
        defaultState = stateManager.defaultState.with(Props.ATTACHED, Direction.DOWN).with(Props.VALID, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.ATTACHED, Props.VALID)
    }

    @SuppressWarnings("deprecation")
    override fun onBlockRemoved(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        val be = world.getBlockEntity(pos)
        if (be is AetherPump) {
            be.unsetTarget()
        }
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(Props.ATTACHED, ctx.side.opposite)
    }

    override fun createBlockEntity(view: BlockView) = AetherPump()

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape {
        return when (state[Props.ATTACHED]!!) {
            Direction.DOWN -> VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.625, 0.75)
            Direction.UP -> VoxelShapes.cuboid(0.25, 0.375, 0.25, 0.75, 0.875, 0.75)

            Direction.NORTH -> VoxelShapes.cuboid(0.25, 0.25, 0.125, 0.75, 0.75, 0.625)
            Direction.SOUTH -> VoxelShapes.cuboid(1-0.25, 0.25, 1-0.125, 1-0.75, 0.75, 1-0.625)

            Direction.WEST -> VoxelShapes.cuboid(0.125, 0.25, 0.25, 0.625, 0.75, 0.75)
            Direction.EAST -> VoxelShapes.cuboid(1-0.125, 0.25, 1-0.25, 1-0.625, 0.75, 1-0.75)
        }
    }

}