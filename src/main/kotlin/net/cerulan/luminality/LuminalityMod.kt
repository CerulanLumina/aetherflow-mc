package net.cerulan.luminality

import net.cerulan.luminality.block.entity.*
import net.cerulan.luminality.block.entity.lumus.LumusPump
import net.cerulan.luminality.block.entity.lumus.LumusRedirector
import net.cerulan.luminality.block.entity.lumus.LumusRegulator
import net.cerulan.luminality.container.LuminalFurnaceController
import net.cerulan.luminality.networking.LuminalityPackets
import net.cerulan.luminality.recipe.LuminalityRecipeTypes
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.container.BlockContext
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object LuminalityMod : ModInitializer {
    override fun onInitialize() {
        registerItems()
        registerBlocks()
        registerBlockEntities()
        LuminalityRecipeTypes.registerRecipes()
        registerScreenControllers()
        LuminalityPackets.registerC2S()
    }

    private fun registerBlocks() {
        registerBlock("shimmer_inducer", LuminalityBlocks.shimmerInducer)
        registerBlock("lumus_pump", LuminalityBlocks.lumusPump)
        registerBlock("lumus_redirector", LuminalityBlocks.lumusRedirector)
        registerBlock("lumus_regulator", LuminalityBlocks.lumusRegulator)
        registerBlock("luminal_furnace", LuminalityBlocks.luminalFurnace)
        registerBlock("lumus_wellspring", LuminalityBlocks.lumusWellspring)
    }

    private fun registerBlockEntities() {
        LuminalityBlocks.BlockEntities.shimmerInducerEntity =
            registerBlockEntity("shimmer_inducer", LuminalityBlocks.shimmerInducer, Supplier { ShimmerInducer() })
        LuminalityBlocks.BlockEntities.luminalFurnaceEntity =
            registerBlockEntity("luminal_furnace", LuminalityBlocks.luminalFurnace, Supplier { LuminalFurnace() })
        LuminalityBlocks.BlockEntities.lumusPumpEntity =
            registerBlockEntity("lumus_pump", LuminalityBlocks.lumusPump, Supplier { LumusPump() })
        LuminalityBlocks.BlockEntities.lumusRedirectorEntity =
            registerBlockEntity("lumus_redirector", LuminalityBlocks.lumusRedirector, Supplier { LumusRedirector() })
        LuminalityBlocks.BlockEntities.lumusRegulatorEntity =
            registerBlockEntity("lumus_regulator", LuminalityBlocks.lumusRegulator, Supplier { LumusRegulator() })
    }

    private fun registerScreenControllers() {
        ContainerProviderRegistry.INSTANCE.registerFactory(
            Identifier(
                "luminality",
                "luminal_furnace"
            )
        ) { syncId, _, player, buf ->
            LuminalFurnaceController(
                syncId,
                player.inventory,
                BlockContext.create(player.world, buf.readBlockPos())
            )
        }
    }

    private fun registerItems() {
        registerItem("shimmering_ingot", LuminalityItems.SHIMMERING_INGOT)
    }

    private fun <T : BlockEntity> registerBlockEntity(id: String, block: Block, sup: Supplier<T>): BlockEntityType<T> {
        return Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            Identifier("luminality", id),
            BlockEntityType.Builder.create(sup, block).build(null)
        )
    }

    private fun registerBlock(id: String, block: Block, item: Boolean = true) {
        val i = Identifier("luminality", id)
        register(Registry.BLOCK, i, block)
        if (item) registerBlockItem(i, block)
    }

    private fun registerBlockItem(id: Identifier, block: Block) {
        register(Registry.ITEM, id, BlockItem(block, Item.Settings().group(LuminalityItems.LUMINALITY_GROUP)))
    }

    private fun registerItem(id: String, item: Item) {
        register(Registry.ITEM, Identifier("luminality", id), item)
    }

    private fun <T> register(reg: Registry<T>, id: Identifier, obj: T) {
        Registry.register(reg, id, obj)
    }

}