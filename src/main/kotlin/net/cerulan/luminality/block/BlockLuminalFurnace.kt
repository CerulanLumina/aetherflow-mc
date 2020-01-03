package net.cerulan.luminality.block

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.luminality.block.entity.LuminalFurnace
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

object BlockLuminalFurnace :
    Block(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByHand(true).strength(1f, 5f).sounds(BlockSoundGroup.METAL).build()),
    AttributeProvider,
    InventoryProvider,
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
        return LuminalFurnace()
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
        val be = world.getBlockEntity(blockPos)
        if (be is LuminalFurnace) {
            ContainerProviderRegistry.INSTANCE.openContainer(Identifier("luminality", "luminal_furnace"), player) {
                buf -> buf.writeBlockPos(blockPos)
            }
        }
        return ActionResult.SUCCESS
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState.with(Props.FACING, ctx.playerFacing.opposite)

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, to: AttributeList<*>) {
        val be = world.getBlockEntity(pos)
        if (be is LuminalFurnace) {
            to.offer(be.lumusSink)
            be.inventory.addAllAttributes(to)
        }
    }

    override fun getInventory(state: BlockState, world: IWorld, pos: BlockPos): SidedInventory? {
        val be = world.getBlockEntity(pos)
        if (be is LuminalFurnace) {
            return be.inventory
        }
        return null
    }

    override fun onBlockRemoved(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean
    ) {
        if (newState.block == this || world.isClient) return
        val be = world.getBlockEntity(pos)
        if (be is LuminalFurnace) {
            ItemScatterer.spawn(world, pos, be.inventory)
        }
        super.onBlockRemoved(state, world, pos, newState, moved)
    }

}