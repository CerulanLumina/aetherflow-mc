package net.cerulan.luminality.block.lumus

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
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
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockLumusRedirector : Block(
    FabricBlockSettings.of(Material.GLASS).nonOpaque().breakByHand(true).strength(
        0.5f,
        10f
    ).sounds(BlockSoundGroup.METAL).build()
),  AttributeProvider,
    BlockEntityProvider {

    object Props {
        val output = IntProperty.of("output",0, 3)!!
    }

    init {
        defaultState = BlockLumusRedirector.stateManager.defaultState.with(Props.output, 0).with(BlockLumusPump.Props.input, Direction.WEST).with(BlockLumusPump.Props.valid, false)
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
        return defaultState.with(BlockLumusPump.Props.input, ctx.side.opposite)
    }

    override fun createBlockEntity(view: BlockView) = LumusRedirector()

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape {
        return super.getOutlineShape(state, view, pos, ePos)
    }
}