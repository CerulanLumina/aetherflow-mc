package net.cerulan.aetherflow.recipe

import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object AetherflowRecipeTypes {
    lateinit var SHIMMER_INDUCER: RecipeType<ShimmerInducerRecipe>
    lateinit var AETHER_FURNACE: RecipeType<AetherFurnaceRecipe>

    fun registerRecipes() {
            val shimmerInducer =
                registerRecipeType("shimmer_inducer", ShimmerInducerRecipe::class.java, ShimmerInducerRecipe.Serializer)
            SHIMMER_INDUCER = shimmerInducer.recipeType
            AetherflowRecipeSerializers.SHIMMER_INDUCER_SERIALIZER = shimmerInducer.recipeSerializer
        val aetherFurnace = registerRecipeType("aether_furnace", AetherFurnaceRecipe::class.java, AetherFurnaceRecipe.Serializer)
        AETHER_FURNACE = aetherFurnace.recipeType
        AetherflowRecipeSerializers.AETHER_FURNACE_SERIALIZER = aetherFurnace.recipeSerializer
    }

    private fun <T : Recipe<*>> registerRecipeType(
        name: String,
        type: Class<T>,
        serializer: RecipeSerializer<T>
    ): TypeSerializer<T> {
        val retSerializer = Registry.register(
            Registry.RECIPE_SERIALIZER,
            Identifier("aetherflow", name),
            serializer
        )
        val retType = Registry.register(
            Registry.RECIPE_TYPE,
            Identifier("aetherflow", name), createRecipeType(name, type)
        )
        return TypeSerializer(retType, retSerializer)
    }

    private fun <T : Recipe<*>> createRecipeType(name: String, type: Class<T>): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString(): String {
                return "aetherflow:$name"
            }
        }
    }

    private data class TypeSerializer<T : Recipe<*>>(
        val recipeType: RecipeType<T>,
        val recipeSerializer: RecipeSerializer<T>
    )

}