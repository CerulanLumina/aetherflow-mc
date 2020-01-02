package net.cerulan.luminality.recipe

import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object LuminalityRecipeTypes {
    lateinit var shimmerInducer: RecipeType<ShimmerInducerRecipe>
    lateinit var luminalFurnace: RecipeType<LuminalFurnaceRecipe>

    fun registerRecipes() {
            val shimmerInducer =
                registerRecipeType("shimmer_inducer", ShimmerInducerRecipe::class.java, ShimmerInducerRecipe.Serializer)
            this.shimmerInducer = shimmerInducer.recipeType
            LuminalityRecipeSerializers.SHIMMER_INDUCER_SERIALIZER = shimmerInducer.recipeSerializer
        val luminalFurnace = registerRecipeType("luminal_furnace", LuminalFurnaceRecipe::class.java, LuminalFurnaceRecipe.Serializer)
        this.luminalFurnace = luminalFurnace.recipeType
        LuminalityRecipeSerializers.luminalFurnaceSerializer = luminalFurnace.recipeSerializer
    }

    private fun <T : Recipe<*>> registerRecipeType(
        name: String,
        type: Class<T>,
        serializer: RecipeSerializer<T>
    ): TypeSerializer<T> {
        val retSerializer = Registry.register(
            Registry.RECIPE_SERIALIZER,
            Identifier("luminality", name),
            serializer
        )
        val retType = Registry.register(
            Registry.RECIPE_TYPE,
            Identifier("luminality", name), createRecipeType(name, type)
        )
        return TypeSerializer(retType, retSerializer)
    }

    private fun <T : Recipe<*>> createRecipeType(name: String, type: Class<T>): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString(): String {
                return "luminality:$name"
            }
        }
    }

    private data class TypeSerializer<T : Recipe<*>>(
        val recipeType: RecipeType<T>,
        val recipeSerializer: RecipeSerializer<T>
    )

}