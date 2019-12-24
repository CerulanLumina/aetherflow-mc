package net.cerulan.aetherflow.blockentity

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.inventory.InventoryWrapper
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Hand
import net.minecraft.util.Tickable

class ShimmerInducer : BlockEntity(AetherflowBlocks.SHIMMER_INDUCER_ENTITY),
    BlockEntityClientSerializable, Tickable {

    companion object {
        val RECIPE_TYPE = AetherflowRecipeTypes.SHIMMER_INDUCER
    }

    private val inv = FullFixedItemInv(1)

    fun interact(player: PlayerEntity) {

        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty) {
            if (inv.getInvStack(0).isEmpty) {
                val stack = player.getStackInHand(Hand.MAIN_HAND).copy()
                if (world!!.recipeManager.getFirstMatch(RECIPE_TYPE, InventoryWrapper.create(inv), world).isPresent) {
                    stack.count = 1
                    inv.setInvStack(0, stack, Simulation.ACTION)
                    player.getStackInHand(Hand.MAIN_HAND).count--
                }
            } else {
                val drop = player.dropStack(inv.getInvStack(0).copy())
                drop!!.setPickupDelay(0)
                inv.extract(1)
            }
        } else if (!inv.getInvStack(0).isEmpty) {
            val drop = player.dropStack(inv.getInvStack(0).copy())
            drop!!.setPickupDelay(0)
            inv.extract(1)
        }
        sync()
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        inv.fromTag(tag.getCompound("inventory"))
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.put("inventory", inv.toTag())
        return super.toTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.put("inventory", inv.toTag())
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        inv.fromTag(tag.getCompound("inventory"))
    }

    var lastTime = 0L

    override fun tick() {
        if (world!!.isClient) return
        if (world!!.timeOfDay > 6000 && lastTime <= 6000) {
            println("lastTime: $lastTime, timeDay: ${world!!.timeOfDay}")
            val res = world!!.recipeManager.getFirstMatch(RECIPE_TYPE, InventoryWrapper.create(inv), world)
            if (res.isPresent) {
                val recipe = res.get()
                inv.setInvStack(0, recipe.output, Simulation.ACTION)
                sync()
            }
        }
        lastTime = world!!.timeOfDay
    }

}