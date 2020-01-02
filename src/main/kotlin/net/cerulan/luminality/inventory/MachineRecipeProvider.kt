package net.cerulan.luminality.inventory

import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.world.World

interface MachineRecipeProvider<T: Recipe<Inventory>> {

    val recipeManager: RecipeManager?
    val recipeType: RecipeType<T>
    val recipeWorld: World?

}