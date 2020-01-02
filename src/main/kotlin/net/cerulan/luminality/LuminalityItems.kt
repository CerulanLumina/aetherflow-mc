package net.cerulan.luminality

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object LuminalityItems {

    val LUMINALITY_GROUP: ItemGroup = FabricItemGroupBuilder.build(Identifier("luminality", "luminality_group")) {
        ItemStack(SHIMMERING_INGOT)
    }

    val SHIMMERING_INGOT = basicItem()

    fun basicItem(): Item {
        return Item(Item.Settings().group(LUMINALITY_GROUP))
    }
}