package net.cerulan.aetherflow.block.entity

import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.cerulan.aetherflow.AetherflowBlocks
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.cerulan.aetherflow.inventory.AttributeSidedInventory
import net.cerulan.aetherflow.inventory.MachineInputItemInv
import net.cerulan.aetherflow.inventory.MachineRecipeProvider
import net.cerulan.aetherflow.recipe.AetherFurnaceRecipe
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.container.PropertyDelegate
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

open class AetherFurnace : BlockEntity(AetherflowBlocks.BlockEntities.AETHER_FURNACE_ENTITY),
    BlockEntityClientSerializable,
    PropertyDelegateHolder,
    Tickable, MachineRecipeProvider<AetherFurnaceRecipe> {

    companion object {
        val minimumRadiance = 4
        val furnaceFlowTicks = 800
    }

    enum class Mode {
        ALL,
        ONLY_FURNACE,
        ONLY_AETHER
    }

    val aetherSink = AetherNode(AetherNodeMode.SINK)
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

    override fun tick() {
        if (world!!.isClient) return
        if (aetherSink.radiance < minimumRadiance) {
            return
        }

        if (!input.getInvStack(0).isEmpty) {
            when (mode) {
                Mode.ALL -> {
                    if (hasAetherRecipe()) {
                        flowTicks += aetherSink.flow
                        maxFlowTicks = getAetherRecipe().flowticks
                    } else if (hasFurnaceRecipe()) {
                        flowTicks += aetherSink.flow
                        maxFlowTicks = furnaceFlowTicks
                    } else {
                        maxFlowTicks = 0
                        flowTicks = 0
                    }
                    if (hasAetherRecipe() && flowTicks == getAetherRecipe().flowticks) {
                        flowTicks = 0
                        output.insert(0, getAetherRecipe().output)
                        input.extract(1)
                    } else if (hasFurnaceRecipe() && flowTicks == furnaceFlowTicks) {
                        flowTicks = 0
                        output.insert(0, getSmeltingRecipe().output)
                        input.extract(1)
                    }
                }
                Mode.ONLY_FURNACE -> {
                    if (hasFurnaceRecipe()) {
                        flowTicks += aetherSink.flow
                        maxFlowTicks = furnaceFlowTicks
                    }
                    else {
                        flowTicks = 0
                        maxFlowTicks = 0
                    }
                    if (hasFurnaceRecipe() && flowTicks == furnaceFlowTicks) {
                        flowTicks = 0
                        output.insert(0, getSmeltingRecipe().output)
                        input.extract(1)

                    }
                }
                Mode.ONLY_AETHER -> {
                    if (hasAetherRecipe()) {
                        flowTicks += aetherSink.flow
                        maxFlowTicks = getAetherRecipe().flowticks
                    } else {
                        flowTicks = 0
                        maxFlowTicks = 0
                    }
                    if (hasAetherRecipe() && flowTicks == getAetherRecipe().flowticks) {
                        flowTicks = 0
                        output.insert(0, getAetherRecipe().output)
                        input.extract(1)
                    }
                }
            }
        }
    }

    private fun getAetherRecipe(): AetherFurnaceRecipe = world!!.recipeManager.getFirstMatch(AetherflowRecipeTypes.AETHER_FURNACE, inventory, world!!).get()
    private fun getSmeltingRecipe(): SmeltingRecipe = world!!.recipeManager.getFirstMatch(RecipeType.SMELTING, inventory, world!!).get()

    private fun hasFurnaceRecipe(): Boolean {
        val recipe = world!!.recipeManager.getFirstMatch(RecipeType.SMELTING, inventory, world!!)
        if (recipe.isPresent) {
            return canPlaceItem(recipe.get().output)
        }
        return false
    }

    private fun hasAetherRecipe(): Boolean {
        val recipe = world!!.recipeManager.getFirstMatch(AetherflowRecipeTypes.AETHER_FURNACE, inventory, world!!)
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
    override val recipeType: RecipeType<AetherFurnaceRecipe>
        get() = AetherflowRecipeTypes.AETHER_FURNACE
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
        override fun size(): Int = 2

        override fun get(key: Int): Int {
            return when (key) {
                0 -> flowTicks
                1 -> maxFlowTicks
                else -> throw IllegalStateException("Property out of bounds")
            }
        }

        override fun set(key: Int, value: Int) {
            when (key) {
                0 -> flowTicks = value
                1 -> maxFlowTicks = value
            }
        }

    }

    override fun getPropertyDelegate(): PropertyDelegate = propertyDelegate

}