package net.cerulan.luminality.block.lumus

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.luminality.LuminalityUtil
import net.cerulan.luminality.LuminalityUtil.rotateRelativeClockwise
import net.cerulan.luminality.api.attr.LumusPumpMarker
import net.cerulan.luminality.block.entity.LumusRedirector
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.EntityContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

object LumusRedirectorBlock : Block(
    FabricBlockSettings.of(Material.GLASS)
        .nonOpaque()
        .breakByHand(true).strength(
        0.5f,
        10f
    ).sounds(BlockSoundGroup.METAL).build()
), AttributeProvider,
    BlockEntityProvider {

    object Props {
        val side1 = DirectionProperty.of("side1") { true }
        val side2 = DirectionProperty.of("side2") { true }
    }

    private val outlineMap: EnumMap<Direction, VoxelShape> = EnumMap(Direction::class.java)

    init {
        outlineMap[Direction.DOWN] = VoxelShapes.cuboid(0.25, 0.0625, 0.25, 0.75, 0.625, 0.75)
        outlineMap[Direction.UP] = VoxelShapes.cuboid(0.25, 1-0.0625, 0.25, 0.75, 1-0.625, 0.75)
        outlineMap[Direction.EAST] = VoxelShapes.cuboid(1-0.0625, 0.25, 0.25, 1-0.625, 0.75, 0.75)
        outlineMap[Direction.WEST] = VoxelShapes.cuboid(0.0625, 0.25, 0.25, 0.625, 0.75, 0.75)
        outlineMap[Direction.NORTH] = VoxelShapes.cuboid(0.25, 0.25, 0.0625, 0.75, 0.75, 0.625)
        outlineMap[Direction.SOUTH] = VoxelShapes.cuboid(0.25, 0.25, 1-0.0625, 0.75, 0.75, 1-0.625)
        defaultState = LumusRedirectorBlock.stateManager.defaultState.with(Props.side1, Direction.WEST)
            .with(Props.side2, Direction.NORTH).with(LumusPumpBlock.Props.valid, false)

    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.side1, Props.side2, LumusPumpBlock.Props.valid)
    }

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, list: AttributeList<*>) {
        list.offer(LumusPumpMarker)
        val be = world.getBlockEntity(pos)
        if (be is LumusRedirector && list.searchDirection == be.inputDirection) {
            list.offer(be.inputSink)
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {

        val hx = MathHelper.fractionalPart(ctx.hitPos.x)
        val hy = MathHelper.fractionalPart(ctx.hitPos.y)
        val hz = MathHelper.fractionalPart(ctx.hitPos.z)

        val side1 = LuminalityUtil.getDirectionFromHitPos(ctx.side, hx, hy, hz)
        val side2 = side1.rotateRelativeClockwise(ctx.side)

        return defaultState.with(Props.side1, side1).with(Props.side2, side2)
    }

    override fun createBlockEntity(view: BlockView) = LumusRedirector()

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
        if (be is LumusRedirector) {
            be.onBroken()
        }
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape {

        val inD = state[Props.side1]
        val input = outlineMap[inD]
        val output = outlineMap[state[Props.side2]]

        return VoxelShapes.union(input, output)
    }
}
