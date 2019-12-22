package net.cerulan.aetherflow.block

import net.cerulan.aetherflow.blockentity.ShimmerInducer
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockShimmerInducer :
    Block(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByHand(true).strength(0.33f, 5f).build()),
    BlockEntityProvider {
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
    ): VoxelShape {
        return VoxelShapes.cuboid(0.3125, 0.0, 0.3125, 0.6875, 0.6875, 0.6875)
    }

}