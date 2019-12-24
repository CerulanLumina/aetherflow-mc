package net.cerulan.aetherflow.block

import net.cerulan.aetherflow.blockentity.AetherFurnace
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

object BlockAetherFurnace :
    Block(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByHand(true).strength(1f, 5f).build()),
    BlockEntityProvider {

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


}