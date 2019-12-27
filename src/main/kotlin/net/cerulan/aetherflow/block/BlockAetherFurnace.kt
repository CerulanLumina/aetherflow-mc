package net.cerulan.aetherflow.block

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.aetherflow.block.entity.AetherFurnace
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockAetherFurnace :
    Block(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByHand(true).strength(1f, 5f).sounds(BlockSoundGroup.METAL).build()),
    AttributeProvider,
    BlockEntityProvider {

    object Props {
        val ACTIVE: BooleanProperty = BooleanProperty.of("active")
        val FACING: DirectionProperty = DirectionProperty.of("facing", Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH)
    }

    init {
        defaultState = stateManager.defaultState.with(Props.ACTIVE, false).with(Props.FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Props.ACTIVE)
            .add(Props.FACING)
    }

    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return AetherFurnace()
    }

    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        /* TODO */
        return ActionResult.SUCCESS
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(Props.FACING, ctx.playerFacing.opposite)

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, to: AttributeList<*>) {
        val be = world.getBlockEntity(pos)
        if (be is AetherFurnace) {
            to.offer(be.aetherNode)
        }
    }

}