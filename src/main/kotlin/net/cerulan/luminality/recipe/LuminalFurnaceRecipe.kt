package net.cerulan.luminality.recipe

import com.google.gson.JsonObject
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

class LuminalFurnaceRecipe(
    private val identifier: Identifier,
    private val grp: String,
    private val input: Ingredient,
    private val out: ItemStack,
    val flowticks: Int
) : Recipe<Inventory> {

    override fun craft(inv: Inventory) = output.copy()

    override fun getId(): Identifier = identifier

    override fun getType(): RecipeType<*> {
        return LuminalityRecipeTypes.luminalFurnace
    }

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer(): RecipeSerializer<*> {
        return LuminalityRecipeSerializers.luminalFurnaceSerializer
    }

    override fun getOutput() = out.copy()

    override fun matches(inv: Inventory, world: World) = input.test(inv.getInvStack(0))

    override fun getGroup() = grp

    object Serializer : RecipeSerializer<LuminalFurnaceRecipe> {
        override fun write(buf: PacketByteBuf, recipe: LuminalFurnaceRecipe) {
            buf.writeString(recipe.group)
            buf.writeItemStack(recipe.output)
            recipe.input.write(buf)
            buf.writeInt(recipe.flowticks)
        }

        override fun read(id: Identifier, json: JsonObject): LuminalFurnaceRecipe {
            val inp = Ingredient.fromJson(json.get("ingredient"))
            val outName = json.get("result").asString
            val grp = json.get("group").asString
            val flowticks = json.get("flowticks").asInt
            val itemStack = ItemStack(
                Registry.ITEM.getOrEmpty(Identifier(outName)).orElseThrow { IllegalStateException("Item: $outName does not exist") } as ItemConvertible
            )
            return LuminalFurnaceRecipe(id, grp, inp, itemStack, flowticks)
        }

        override fun read(id: Identifier, buf: PacketByteBuf): LuminalFurnaceRecipe {
            val grp = buf.readString()
            val out = buf.readItemStack()
            val inp = Ingredient.fromPacket(buf)
            val flowticks = buf.readInt()
            return LuminalFurnaceRecipe(id, grp, inp, out, flowticks)
        }

    }

}

