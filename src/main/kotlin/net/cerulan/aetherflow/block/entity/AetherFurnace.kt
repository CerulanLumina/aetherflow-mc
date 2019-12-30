package net.cerulan.aetherflow.block.entity

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.ItemAttributes
import alexiil.mc.lib.attributes.item.ItemInvUtil
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.cerulan.aetherflow.inventory.AttributeSidedInventory
import net.cerulan.aetherflow.inventory.InventoryWrapper
import net.cerulan.aetherflow.inventory.MachineInputItemInv
import net.cerulan.aetherflow.inventory.MachineRecipeProvider
import net.cerulan.aetherflow.recipe.AetherFurnaceRecipe
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

class AetherFurnace : BlockEntity(AetherflowBlocks.BlockEntities.AETHER_FURNACE_ENTITY),
    Tickable, MachineRecipeProvider<AetherFurnaceRecipe> {

    val aetherSink = AetherNode(AetherNodeMode.SINK)

    val input = MachineInputItemInv(this)
    val output = DirectFixedItemInv(1)
    val inventory: AttributeSidedInventory = AttributeSidedInventory(false, input, output)

    init {
        inventory.insertable[0] = true
        inventory.extractable[0] = false
        inventory.insertable[1] = false
        inventory.extractable[1] = true
        Arrays.stream(Direction.values()).filter { d -> d != Direction.DOWN}.forEach { d -> inventory.directions[d] = 0}
        inventory.directions[Direction.DOWN] = 1
    }

    override fun tick() {
        if (world!!.isClient) return
        val recipe = world!!.recipeManager.getFirstMatch(AetherflowRecipeTypes.AETHER_FURNACE, inventory, world!!)
        if (recipe.isPresent) {
            val extract = input.attemptAnyExtraction(1, Simulation.SIMULATE)
            val insert = output.attemptInsertion(recipe.get().output, Simulation.SIMULATE)
            if (!extract.isEmpty && insert.isEmpty) {
                input.attemptAnyExtraction(1, Simulation.ACTION)
                output.attemptInsertion(recipe.get().output, Simulation.ACTION)
            }
        }
    }

    override val recipeManager: RecipeManager?
        get() = world?.recipeManager
    override val recipeType: RecipeType<AetherFurnaceRecipe>
        get() = AetherflowRecipeTypes.AETHER_FURNACE
    override val recipeWorld: World?
        get() = world

}