package net.cerulan.aetherflow.blockentity

import alexiil.mc.lib.attributes.Simulation
import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.inventory.InventoryWrapper
import net.cerulan.aetherflow.inventory.ShimmerInducerItemInv
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

    val inventory = ShimmerInducerItemInv(this)
    var outputting = false

    init {
        inventory.addListener(
            { view, slot, previous, current ->
                run {
                    sync()
                    if (!previous.isEmpty && current.isEmpty) {
                        outputting = false
                    }
                }
            },
            { })
    }

    fun interact(player: PlayerEntity) {
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty) {
            if (inventory.getInvStack(0).isEmpty) {
                val stack = player.getStackInHand(Hand.MAIN_HAND).copy()
                stack.count = 1
                if (inventory.setInvStack(0, stack, Simulation.ACTION)) {
                    --player.getStackInHand(Hand.MAIN_HAND).count
                }
            } else {
                val ext = inventory.extract(1)
                if (!ext.isEmpty) {
                    val drop = player.dropStack(ext)
                    drop!!.setPickupDelay(0)
                }

            }
        } else if (!inventory.getInvStack(0).isEmpty) {
            val ext = inventory.extract(1)
            if (!ext.isEmpty) {
                val drop = player.dropStack(ext)
                drop!!.setPickupDelay(0)
            }
        }
        sync()
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        inventory.fromTag(tag.getCompound("inventory"))
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.put("inventory", inventory.toTag())
        return super.toTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.put("inventory", inventory.toTag())
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        inventory.fromTag(tag.getCompound("inventory"))
    }

    var lastTime = 0L

    override fun tick() {
        if (world!!.isClient) return
        if (world!!.timeOfDay > 6000 && lastTime <= 6000) {
            println("lastTime: $lastTime, timeDay: ${world!!.timeOfDay}")
            val res = world!!.recipeManager.getFirstMatch(RECIPE_TYPE, InventoryWrapper.create(inventory), world)
            if (res.isPresent) {
                val recipe = res.get()
                outputting = true
                inventory.setInvStack(0, recipe.output, Simulation.ACTION)
                sync()
            }
        }
        lastTime = world!!.timeOfDay
    }

}