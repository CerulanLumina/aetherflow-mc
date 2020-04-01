package net.cerulan.luminality.block.entity

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv
import com.google.common.collect.ImmutableList
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.cerulan.luminality.api.attr.LumusSink
import net.cerulan.luminality.inventory.AttributeSidedInventory
import net.cerulan.luminality.inventory.MachineInputItemInv
import net.cerulan.luminality.inventory.MachineRecipeProvider
import net.cerulan.luminality.networking.PacketConfigurable
import net.cerulan.luminality.recipe.LuminalFurnaceRecipe
import net.cerulan.luminality.recipe.LuminalityRecipeTypes
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.container.PropertyDelegate
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

open class LuminalFurnace : BlockEntity(LuminalityBlocks.BlockEntities.luminalFurnaceEntity),
    BlockEntityClientSerializable,
    PropertyDelegateHolder,
    PacketConfigurable,
    Tickable, MachineRecipeProvider {

    companion object {
        const val minimumRadiance = 4
        const val furnaceFlowTicks = 800
    }

    enum class Mode(val recipeTypes: ImmutableList<RecipeType<out Recipe<Inventory>>>) {
        ALL(ImmutableList.builder<RecipeType<out Recipe<Inventory>>>().add(LuminalityRecipeTypes.luminalFurnace, RecipeType.SMELTING).build()),
        ONLY_FURNACE(ImmutableList.builder<RecipeType<out Recipe<Inventory>>>().add(RecipeType.SMELTING).build()),
        ONLY_LUMINAL(ImmutableList.builder<RecipeType<out Recipe<Inventory>>>().add(LuminalityRecipeTypes.luminalFurnace).build())
    }

    val lumusSink = LumusSink()
    var mode = Mode.ALL

    var maxFlowTicks = 0

    private val input = MachineInputItemInv(this)
    private val output = DirectFixedItemInv(1)
    val inventory: AttributeSidedInventory = AttributeSidedInventory(false, input, output)
    var flowTicks = 0
        protected set

    init {
        inventory.insertable[0] = true
        inventory.extractable[0] = false
        inventory.insertable[1] = false
        inventory.extractable[1] = true
        Arrays.stream(Direction.values()).filter { d -> d != Direction.DOWN}.forEach { d -> inventory.directions[d] = 0}
        inventory.directions[Direction.DOWN] = 1
    }

    var hasReset = false

    override fun tick() {
        if (world!!.isClient) return

        if (lumusSink.power.radiance < minimumRadiance && !hasReset) {
            flowTicks = 0
            maxFlowTicks = 0
            hasReset = true
            sync()
            return
        }
        hasReset = false
        if (!input.getInvStack(0).isEmpty) {
            when (mode) {
                Mode.ALL -> {
                    if (hasLuminalRecipe()) {
                        flowTicks += lumusSink.power.flow
                        maxFlowTicks = getLuminalRecipe().flowticks
                    } else if (hasFurnaceRecipe()) {
                        flowTicks += lumusSink.power.flow
                        maxFlowTicks = furnaceFlowTicks
                    } else {
                        maxFlowTicks = 0
                        flowTicks = 0
                    }
                    if (hasLuminalRecipe() && flowTicks == getLuminalRecipe().flowticks) {
                        flowTicks = 0
                        output.insert(0, getLuminalRecipe().output)
                        input.extract(1)
                    } else if (hasFurnaceRecipe() && flowTicks >= furnaceFlowTicks) {
                        flowTicks = 0
                        output.insert(0, getSmeltingRecipe().output)
                        input.extract(1)
                    }
                }
                Mode.ONLY_FURNACE -> {
                    if (hasFurnaceRecipe()) {
                        flowTicks += lumusSink.power.flow
                        maxFlowTicks = furnaceFlowTicks
                    }
                    else {
                        flowTicks = 0
                        maxFlowTicks = 0
                    }
                    if (hasFurnaceRecipe() && flowTicks >= furnaceFlowTicks) {
                        flowTicks = 0
                        output.insert(0, getSmeltingRecipe().output)
                        input.extract(1)

                    }
                }
                Mode.ONLY_LUMINAL -> {
                    if (hasLuminalRecipe()) {
                        flowTicks += lumusSink.power.flow
                        maxFlowTicks = getLuminalRecipe().flowticks
                    } else {
                        flowTicks = 0
                        maxFlowTicks = 0
                    }
                    if (hasLuminalRecipe() && flowTicks >= getLuminalRecipe().flowticks) {
                        flowTicks = 0
                        output.insert(0, getLuminalRecipe().output)
                        input.extract(1)
                    }
                }
            }
        } else {
            flowTicks = 0
            maxFlowTicks = 0
        }
    }

    private fun getLuminalRecipe(): LuminalFurnaceRecipe = world!!.recipeManager.getFirstMatch(LuminalityRecipeTypes.luminalFurnace, inventory, world!!).get()
    private fun getSmeltingRecipe(): SmeltingRecipe = world!!.recipeManager.getFirstMatch(RecipeType.SMELTING, inventory, world!!).get()

    private fun hasFurnaceRecipe(): Boolean {
        val recipe = world!!.recipeManager.getFirstMatch(RecipeType.SMELTING, inventory, world!!)
        if (recipe.isPresent) {
            return canPlaceItem(recipe.get().output)
        }
        return false
    }

    private fun hasLuminalRecipe(): Boolean {
        val recipe = world!!.recipeManager.getFirstMatch(LuminalityRecipeTypes.luminalFurnace, inventory, world!!)
        if (recipe.isPresent) {
            return canPlaceItem(recipe.get().output)
        }
        return false
    }

    private fun canPlaceItem(recipeOutput: ItemStack): Boolean {
        val extract = input.attemptAnyExtraction(1, Simulation.SIMULATE)
        val insert = output.attemptInsertion(recipeOutput, Simulation.SIMULATE)
        return !extract.isEmpty && insert.isEmpty
    }

    override val recipeManager: RecipeManager?
        get() = world?.recipeManager
    override val recipeTypes: ImmutableList<RecipeType<out Recipe<Inventory>>>
        get() = mode.recipeTypes
    override val recipeWorld: World?
        get() = world

    override fun toClientTag(p0: CompoundTag): CompoundTag {
        p0.putInt("mode", mode.ordinal)
        p0.putInt("flowticks", flowTicks)
        p0.putInt("maxflowticks", maxFlowTicks)
        return p0
    }

    override fun fromClientTag(p0: CompoundTag) {
        mode = Mode.values()[p0.getInt("mode")]
        flowTicks = p0.getInt("flowticks")
        maxFlowTicks = p0.getInt("maxflowticks")
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val ntag = super.toTag(tag)
        ntag.putInt("mode", mode.ordinal)
        ntag.putInt("flowticks", flowTicks)
        ntag.put("inputInv", input.toTag())
        ntag.put("outputInv", output.toTag())
        return ntag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        mode = Mode.values()[tag.getInt("mode")]
        flowTicks = tag.getInt("flowticks")
        input.fromTag(tag.getCompound("inputInv"))
        output.fromTag(tag.getCompound("outputInv"))
    }

    private val propertyDelegate = object : PropertyDelegate {
        override fun size(): Int = 3

        override fun get(key: Int): Int {
            return when (key) {
                0 -> flowTicks
                1 -> maxFlowTicks
                2 -> mode.ordinal
                else -> throw IllegalStateException("Property out of bounds")
            }
        }

        override fun set(key: Int, value: Int) {
            when (key) {
                0 -> flowTicks = value
                1 -> maxFlowTicks = value
                2 -> mode = Mode.values()[value]
            }
        }

    }

    override fun getPropertyDelegate(): PropertyDelegate = propertyDelegate

    override fun configureFromPacket(byteArray: ByteArray) {
        val b = byteArray[0]
        if (b in 0..2)
            propertyDelegate.set(2, b.toInt())
        else throw IllegalStateException("Bad mode ID")
        sync()
    }

    override fun expectedBytesForType(type: Byte): Int {
        return when (type) {
            0.toByte() -> 1
            else -> 0
        }
    }

}