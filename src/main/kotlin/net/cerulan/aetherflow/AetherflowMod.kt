package net.cerulan.aetherflow

import net.cerulan.aetherflow.block.entity.AetherFurnace
import net.cerulan.aetherflow.block.entity.AetherPump
import net.cerulan.aetherflow.block.entity.ShimmerInducer
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.fabricmc.api.ModInitializer
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object AetherflowMod : ModInitializer {
    override fun onInitialize() {
        registerItems()
        registerBlocks()
        registerBlockEntities()
        AetherflowRecipeTypes.registerRecipes()
    }

    private fun registerBlocks() {
        registerBlock("shimmer_inducer", AetherflowBlocks.SHIMMER_INDUCER)
        registerBlock("aether_furnace", AetherflowBlocks.AETHER_FURNACE)
        registerBlock("aether_pump", AetherflowBlocks.AETHER_PUMP)
    }

    private fun registerBlockEntities() {
        AetherflowBlocks.BlockEntities.SHIMMER_INDUCER_ENTITY =
            registerBlockEntity("shimmer_inducer", AetherflowBlocks.SHIMMER_INDUCER, Supplier { ShimmerInducer() })
        AetherflowBlocks.BlockEntities.AETHER_FURNACE_ENTITY =
            registerBlockEntity("aether_furnace", AetherflowBlocks.AETHER_FURNACE, Supplier { AetherFurnace() })
        AetherflowBlocks.BlockEntities.AETHER_PUMP_ENTITY =
            registerBlockEntity("aether_pump", AetherflowBlocks.AETHER_PUMP, Supplier { AetherPump() })
    }

    private fun registerItems() {
        registerItem("shimmering_ingot", AetherflowItems.SHIMMERING_INGOT)
    }

    private fun <T : BlockEntity> registerBlockEntity(id: String, block: Block, sup: Supplier<T>): BlockEntityType<T> {
        return Registry.register(
            Registry.BLOCK_ENTITY,
            Identifier("aetherflow", id),
            BlockEntityType.Builder.create(sup, block).build(null)
        )
    }

    private fun registerBlock(id: String, block: Block, item: Boolean = true) {
        val i = Identifier("aetherflow", id)
        register(Registry.BLOCK, i, block)
        if (item) registerBlockItem(i, block)
    }

    private fun registerBlockItem(id: Identifier, block: Block) {
        register(Registry.ITEM, id, BlockItem(block, Item.Settings().group(AetherflowItems.AETHERFLOW_GROUP)))
    }

    private fun registerItem(id: String, item: Item) {
        register(Registry.ITEM, Identifier("aetherflow", id), item)
    }

    private fun <T> register(reg: Registry<T>, id: Identifier, obj: T) {
        Registry.register(reg, id, obj)
    }

}