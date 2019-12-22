package net.cerulan.aetherflow.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList
import kotlin.math.min

interface SimpleInventory : Inventory {
    fun getItems(): DefaultedList<ItemStack>

    override fun getInvSize() = getItems().size
    override fun isInvEmpty() = getItems().all(ItemStack::isEmpty)
    override fun getInvStack(slot: Int) = getItems().get(slot)

    override fun takeInvStack(slot: Int, amount: Int): ItemStack {
        val res = Inventories.splitStack(getItems(), slot, amount)
        if (!res.isEmpty) markDirty()
        return res
    }

    override fun removeInvStack(slot: Int) = Inventories.removeStack(getItems(), slot)

    override fun setInvStack(slot: Int, stack: ItemStack) {
        getItems()[slot] = stack
        stack.count = min(invMaxStackAmount, stack.count)
    }

    override fun clear() = getItems().clear()

    override fun markDirty() {
        // Default is no behavior
    }

    override fun canPlayerUseInv(player: PlayerEntity?) = true

}