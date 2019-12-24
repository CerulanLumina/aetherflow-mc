package net.cerulan.aetherflow.inventory

import alexiil.mc.lib.attributes.item.FixedItemInv
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory

object InventoryWrapper {

    fun create(inv: FixedItemInv): Inventory {
        return object : InventoryFixedWrapper(inv) {
            override fun canPlayerUseInv(player: PlayerEntity?): Boolean {
                return true
            }
        }
    }

}