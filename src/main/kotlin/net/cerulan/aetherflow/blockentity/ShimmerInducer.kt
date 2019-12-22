package net.cerulan.aetherflow.blockentity

import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.inventory.SimpleInventory
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.DefaultedList
import net.minecraft.util.Hand
import net.minecraft.util.Tickable

class ShimmerInducer: BlockEntity(AetherflowBlocks.SHIMMER_INDUCER_ENTITY), SimpleInventory,
    BlockEntityClientSerializable, Tickable {

    companion object {
        val RECIPE_TYPE = AetherflowRecipeTypes.SHIMMER_INDUCER
    }

    private val items = DefaultedList.ofSize(1, ItemStack.EMPTY)

    override fun getItems(): DefaultedList<ItemStack> = items

    fun interact(player: PlayerEntity) {
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty) {
            if (getInvStack(0).isEmpty) {
                val stack = player.getStackInHand(Hand.MAIN_HAND).copy()
                val testInv = CraftingResultInventory()
                testInv.setInvStack(0, stack)
                if (world!!.recipeManager.getFirstMatch(RECIPE_TYPE, testInv, world).isPresent) {
                    stack.count = 1
                    setInvStack(0, stack)
                    player.getStackInHand(Hand.MAIN_HAND).count--
                }
            } else {
                val drop = player.dropStack(getInvStack(0).copy())
                drop!!.setPickupDelay(0)
                getInvStack(0).count = 0
            }
        } else if (!getInvStack(0).isEmpty) {
            val drop = player.dropStack(getInvStack(0).copy())
            drop!!.setPickupDelay(0)
            getInvStack(0).count = 0
        }
        sync()
    }

    override fun markDirty() {
        super<BlockEntity>.markDirty()
        // nothing atm
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        Inventories.fromTag(tag, items)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        Inventories.toTag(tag, items)
        return super.toTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        val itemTag = CompoundTag()
        Inventories.toTag(itemTag, items)
        tag.put("item", itemTag)
        return tag
    }

    override fun getInvMaxStackAmount() = 1

    override fun fromClientTag(tag: CompoundTag) {
        clear()
        Inventories.fromTag(tag.getCompound("item"), items)
    }

    override fun tick() {
        val res = world!!.recipeManager.getFirstMatch(RECIPE_TYPE, this, world)
        if (res.isPresent) {
            val recipe = res.get()
            setInvStack(0, recipe.output)
        }
    }

}