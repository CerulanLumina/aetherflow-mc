package net.cerulan.aetherflow.block

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.aetherflow.blockentity.ShimmerInducer
import net.cerulan.aetherflow.inventory.InventoryWrapper
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World


object BlockShimmerInducer :
    Block(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByHand(true).strength(0.33f, 5f).build()),
    BlockEntityProvider,
    AttributeProvider,
    InventoryProvider {

    override fun createBlockEntity(view: BlockView?): BlockEntity? {
        return ShimmerInducer()
    }

    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (world.isClient || hand == Hand.OFF_HAND) return ActionResult.SUCCESS
        val inv = world.getBlockEntity(blockPos) as ShimmerInducer
        inv.interact(player)
        return ActionResult.SUCCESS
    }

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        ePos: EntityContext
    ): VoxelShape = VoxelShapes.cuboid(0.3125, 0.0, 0.3125, 0.6875, 0.6875, 0.6875)

    override fun addAllAttributes(world: World, pos: BlockPos, state: BlockState, to: AttributeList<*>) {
        val be = world.getBlockEntity(pos)
        if (be is ShimmerInducer) {
            to.offer(be.inventory)
        }
    }

    override fun getInventory(state: BlockState?, world: IWorld?, pos: BlockPos?): SidedInventory? {
        val be = world!!.getBlockEntity(pos)
        if (be is ShimmerInducer) {
            return InventoryWrapper.create(be.inventory)
        }
        return null
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState?, world: World?, pos: BlockPos?): Int {
        val be = world?.getBlockEntity(pos)
        if (be is ShimmerInducer) {
            return if (be.outputting) 15 else 0
        } else return 0
    }

}