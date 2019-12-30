package net.cerulan.aetherflow.recipe

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv
import com.google.gson.JsonObject
import net.cerulan.aetherflow.inventory.InventoryWrapper
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class AetherFurnaceRecipe(
    private val identifier: Identifier,
    private val grp: String,
    private val input: Ingredient,
    private val out: ItemStack
) : Recipe<Inventory> {


    override fun craft(inv: Inventory) = output.copy()

    override fun getId(): Identifier = identifier

    override fun getType(): RecipeType<*> {
        return AetherflowRecipeTypes.AETHER_FURNACE
    }

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer(): RecipeSerializer<*> {
        return AetherflowRecipeSerializers.AETHER_FURNACE_SERIALIZER
    }

    override fun getOutput() = out.copy()

    override fun matches(inv: Inventory, world: World) = input.test(inv.getInvStack(0))

    override fun getGroup() = grp

    object Serializer : RecipeSerializer<AetherFurnaceRecipe> {
        override fun write(buf: PacketByteBuf, recipe: AetherFurnaceRecipe) {
            buf.writeString(recipe.group)
            buf.writeItemStack(recipe.output)
            recipe.input.write(buf)
        }

        override fun read(id: Identifier, json: JsonObject): AetherFurnaceRecipe {
            val inp = Ingredient.fromJson(json.get("ingredient"))
            val outName = json.get("result").asString
            val grp = json.get("group").asString
            val itemStack = ItemStack(
                Registry.ITEM.getOrEmpty(Identifier(outName)).orElseThrow { IllegalStateException("Item: $outName does not exist") } as ItemConvertible
            )
            return AetherFurnaceRecipe(id, grp, inp, itemStack)
        }

        override fun read(id: Identifier, buf: PacketByteBuf): AetherFurnaceRecipe {
            val grp = buf.readString()
            val out = buf.readItemStack()
            val inp = Ingredient.fromPacket(buf)
            return AetherFurnaceRecipe(id, grp, inp, out)
        }

    }

}

