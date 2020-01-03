package net.cerulan.luminality.inventory

import alexiil.mc.lib.attributes.item.filter.ItemFilter
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import net.minecraft.item.ItemStack

class MachineInputItemInv(private val recipeProvider: MachineRecipeProvider) : DirectFixedItemInv(1) {

    private val testInv = FullFixedItemInv(1)
    private val testWrapper = InventoryWrapper.create(testInv)
    override fun isItemValidForSlot(slot: Int, item: ItemStack): Boolean {
        if (item.isEmpty) return true
        testInv.forceSetInvStack(0, item.copy())
        return recipeProvider.recipeTypes.map { recipeType ->
            recipeProvider.recipeManager?.getFirstMatch(
                recipeType,
                testWrapper,
                recipeProvider.recipeWorld
            )?.isPresent ?: false
        }
            .any()
    }

    override fun getFilterForSlot(slot: Int): ItemFilter {
        return ItemFilter { stack -> isItemValidForSlot(slot, stack) }
    }

}