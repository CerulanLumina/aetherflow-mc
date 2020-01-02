package net.cerulan.luminality.inventory

import alexiil.mc.lib.attributes.item.filter.ItemFilter
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import net.cerulan.luminality.block.entity.ShimmerInducer
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.item.ItemStack

class ShimmerInducerItemInv(private val shimmerInducer: ShimmerInducer) : FullFixedItemInv(1) {
    override fun isItemValidForSlot(slot: Int, item: ItemStack): Boolean {
        if (shimmerInducer.outputting) return true
        val world = shimmerInducer.world
        val inv = CraftingResultInventory()
        inv.setInvStack(0, item)
        return world?.recipeManager?.getFirstMatch(ShimmerInducer.RECIPE_TYPE, inv, world)?.isPresent ?: false
    }

    override fun getFilterForSlot(slot: Int): ItemFilter {
        return ItemFilter { stack -> isItemValidForSlot(slot, stack) }
    }

    override fun getMaxAmount(slot: Int, stack: ItemStack?) = 1

}