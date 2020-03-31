package net.cerulan.luminality.block.lumus

import net.cerulan.luminality.block.entity.lumus.LumusPump
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.*
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

object LumusPumpBlock :
    AbstractGlassBlock(
        FabricBlockSettings.of(Material.GLASS).nonOpaque().breakByHand(true).strength(
            0.5f,
            10f
        ).sounds(BlockSoundGroup.METAL).build()
    ),
    BlockEntityProvider {

    object Props {
        val input = DirectionProperty.of("input") {true}!!
        val valid = BooleanProperty.of("valid")!!
    }

    init {
        defaultState = stateManager.defaultState.with(Props.input, Direction.DOWN).with(Props.valid, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.input, Props.valid)
    }

    @SuppressWarnings("deprecation")
    override fun onBlockRemoved(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (newState.block == this || world.isClient) return
        val be = world.getBlockEntity(pos)
        if (be is LumusPump) {
            be.onBroken()
        }
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(Props.input, ctx.side.opposite)
    }

    override fun createBlockEntity(view: BlockView) = LumusPump()

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape {
        return when (state[Props.input]!!) {
            Direction.DOWN -> VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.625, 0.75)
            Direction.UP -> VoxelShapes.cuboid(0.25, 0.375, 0.25, 0.75, 0.875, 0.75)

            Direction.NORTH -> VoxelShapes.cuboid(0.25, 0.25, 0.125, 0.75, 0.75, 0.625)
            Direction.SOUTH -> VoxelShapes.cuboid(1-0.25, 0.25, 1-0.125, 1-0.75, 0.75, 1-0.625)

            Direction.WEST -> VoxelShapes.cuboid(0.125, 0.25, 0.25, 0.625, 0.75, 0.75)
            Direction.EAST -> VoxelShapes.cuboid(1-0.125, 0.25, 1-0.25, 1-0.625, 0.75, 1-0.75)
        }
    }

}