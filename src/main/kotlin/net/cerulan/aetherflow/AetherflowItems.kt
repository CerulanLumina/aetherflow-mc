package net.cerulan.aetherflow

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object AetherflowItems {

    val AETHERFLOW_GROUP: ItemGroup = FabricItemGroupBuilder.build(Identifier("aetherflow", "aetherflow_group")) {
        ItemStack(SHIMMERING_INGOT)
    }

    val SHIMMERING_INGOT = basicItem()

    fun basicItem(): Item {
        return Item(Item.Settings().group(AETHERFLOW_GROUP))
    }
}