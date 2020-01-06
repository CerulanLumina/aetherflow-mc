package net.cerulan.luminality.block.lumus

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.luminality.LuminalityUtil
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
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

object BlockLumusRedirector : Block(
    FabricBlockSettings.of(Material.GLASS).nonOpaque().breakByHand(true).strength(
        0.5f,
        10f
    ).sounds(BlockSoundGroup.METAL).build()
), AttributeProvider,
    BlockEntityProvider {

    object Props {
        val output = IntProperty.of("output", 0, 3)!!
    }

    private val outlineMap: EnumMap<Direction, VoxelShape> = EnumMap(Direction::class.java)

    init {
        outlineMap[Direction.DOWN] = VoxelShapes.cuboid(0.25, 0.0625, 0.25, 0.75, 0.625, 0.75)
        outlineMap[Direction.UP] = VoxelShapes.cuboid(0.25, 1-0.0625, 0.25, 0.75, 1-0.625, 0.75)
        outlineMap[Direction.EAST] = VoxelShapes.cuboid(1-0.0625, 0.25, 0.25, 1-0.625, 0.75, 0.75)
        outlineMap[Direction.WEST] = VoxelShapes.cuboid(0.0625, 0.25, 0.25, 0.625, 0.75, 0.75)
        outlineMap[Direction.NORTH] = VoxelShapes.cuboid(0.25, 0.25, 0.0625, 0.75, 0.75, 0.625)
        outlineMap[Direction.SOUTH] = VoxelShapes.cuboid(0.25, 0.25, 1-0.0625, 0.75, 0.75, 1-0.625)
        defaultState = BlockLumusRedirector.stateManager.defaultState.with(Props.output, 0)
            .with(BlockLumusPump.Props.input, Direction.WEST).with(BlockLumusPump.Props.valid, false)

    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.output, BlockLumusPump.Props.input, BlockLumusPump.Props.valid)
    }

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, list: AttributeList<*>) {
        list.offer(LumusPumpMarker)
        val be = world.getBlockEntity(pos)
        if (be is LumusRedirector && list.searchDirection == state[BlockLumusPump.Props.input]) {
            list.offer(be.lumusSink)
        }
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
        if (be is LumusRedirector) {
            be.unsetTarget()
        }
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val hx = MathHelper.fractionalPart(ctx.hitPos.x)
        val hy = MathHelper.fractionalPart(ctx.hitPos.y)
        val hz = MathHelper.fractionalPart(ctx.hitPos.z)
        if (ctx.world.isClient) {
            println("x: $hx")
            println("x: $hy")
            println("x: $hz")
        }
        try {
            return defaultState.with(BlockLumusPump.Props.input, ctx.side.opposite).with(
                Props.output,
                LuminalityUtil.getDirectionRightAngleIndex(
                    ctx.side.opposite,
                    LuminalityUtil.getDirectionFromHitPos(ctx.side, hx, hy, hz)
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return defaultState.with(BlockLumusPump.Props.input, ctx.side.opposite)
        }
    }

    override fun createBlockEntity(view: BlockView) = LumusRedirector()

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape {
        val inD = state[BlockLumusPump.Props.input]
        val input = outlineMap[inD]
        val output = outlineMap[LuminalityUtil.getDirectionRightAngle(state[Props.output], inD)]

        return VoxelShapes.union(input, output)
    }
}