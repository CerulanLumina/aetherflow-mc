package net.cerulan.luminality.inventory

import alexiil.mc.lib.attributes.item.filter.ItemFilter
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe

class MachineInputItemInv<T: Recipe<Inventory>>(private val recipeProvider: MachineRecipeProvider<T>) : DirectFixedItemInv(1) {
    private val testInv = FullFixedItemInv(1)
    private val testWrapper = InventoryWrapper.create(testInv)
    override fun isItemValidForSlot(slot: Int, item: ItemStack): Boolean  {
        if (item.isEmpty) return true
        testInv.forceSetInvStack(0, item.copy())
        return recipeProvider.recipeManager?.getFirstMatch(
            recipeProvider.recipeType,
            testWrapper,
            recipeProvider.recipeWorld
        )?.isPresent ?: false
    }

    override fun getFilterForSlot(slot: Int): ItemFilter {
        return ItemFilter { stack -> isItemValidForSlot(slot, stack) }
    }

}