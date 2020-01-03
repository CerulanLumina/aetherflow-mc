package net.cerulan.luminality.inventory

import alexiil.mc.lib.attributes.item.compat.SidedInventoryFixedWrapper
import com.google.common.collect.ImmutableList
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.world.World

interface MachineRecipeProvider {

    val recipeManager: RecipeManager?
    val recipeTypes: ImmutableList<RecipeType<out Recipe<Inventory>>>
    val recipeWorld: World?

}