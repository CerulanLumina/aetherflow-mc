package net.cerulan.aetherflow.inventory

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.Simulation
import alexiil.mc.lib.attributes.item.FixedItemInv
import alexiil.mc.lib.attributes.item.ItemExtractable
import alexiil.mc.lib.attributes.item.ItemInsertable
import alexiil.mc.lib.attributes.item.ItemTransferable
import alexiil.mc.lib.attributes.item.compat.SidedInventoryFixedWrapper
import alexiil.mc.lib.attributes.item.filter.ItemFilter
import alexiil.mc.lib.attributes.item.impl.CombinedFixedItemInv
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import java.util.*

/**
 * An implementation of SidedInventory using an array of FixedItemInvs.
 * Every side can be mapped to exactly one sub inventory, and every inventory can
 * be marked as insertable, extractable, or both. If a side is not mapped to an
 * inventory, it is automatically mapped to all inventories if `nullDirectionAll`
 * is true, and no inventories if false
 */
class AttributeSidedInventory(private var nullDirectionAll: Boolean, vararg wrapInv: FixedItemInv) : CombinedFixedItemInv<FixedItemInv>(wrapInv.asList()), SidedInventory {

//    private val directions: Array<Array<Direction>?> = Array(wrapInv.size) { null }
    val directions: EnumMap<Direction, Int> = EnumMap(Direction::class.java)

    val insertable: BooleanArray = BooleanArray(wrapInv.size) { true }
    val extractable: BooleanArray = BooleanArray(wrapInv.size) { true }

    private val sidedInventoryFixedWrapper = object : SidedInventoryFixedWrapper(this) {
        override fun canPlayerUseInv(player: PlayerEntity): Boolean = true
    }

    override fun getInvAvailableSlots(side: Direction?): IntArray {
        return if (side == null) {
            getNullSlots()
        } else {
            if (directions[side] == null) getNullSlots()
            else {
                val start = subSlotStartIndex[directions[side]!!]
                val end = start + views[directions[side]!!].slotCount
                (start until end).toList().toIntArray()
            }
        }
    }

    private fun getNullSlots(): IntArray {
        return if (nullDirectionAll) sidedInventoryFixedWrapper.getInvAvailableSlots(null)
        else IntArray(0)
    }

    fun addAllAttributes(list: AttributeList<*>) {
        val dir = list.searchDirection
        if (dir == null || directions[dir] == null) {
            if (nullDirectionAll) list.offer(this)
        } else {
            val index = directions[dir]!!
            if (insertable[index] && extractable[index]) {
                list.offer(AttributeTransferableSide(views[index].transferable))
            } else if (insertable[index]) {
                list.offer(AttributeInsertableSide(views[index].insertable))
            } else if (extractable[index]) {
                list.offer(AttributeExtractableSide(views[index].extractable))
            }
        }
    }

    override fun canExtractInvStack(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return getInvAvailableSlots(dir).contains(slot) && extractable[getInvIndex(slot)] && sidedInventoryFixedWrapper.canExtractInvStack(slot, stack, dir)
    }

    override fun canInsertInvStack(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return getInvAvailableSlots(dir).contains(slot) && insertable[getInvIndex(slot)] && sidedInventoryFixedWrapper.canInsertInvStack(
            slot,
            stack,
            dir
        )
    }

    /// CombinedFixedItemInvView
    override fun isItemValidForSlot(slot: Int, item: ItemStack): Boolean = insertable[getInvIndex(slot)] && getInv(slot).isItemValidForSlot(slot, item)

    /// Inventory interface
    override fun isValidInvStack(slot: Int, stack: ItemStack): Boolean = isItemValidForSlot(slot, stack)

    override fun markDirty() = sidedInventoryFixedWrapper.markDirty()

    override fun clear()  = sidedInventoryFixedWrapper.clear()

    override fun setInvStack(slot: Int, stack: ItemStack) = sidedInventoryFixedWrapper.setInvStack(slot, stack)

    override fun removeInvStack(slot: Int): ItemStack = sidedInventoryFixedWrapper.removeInvStack(slot)

    override fun canPlayerUseInv(player: PlayerEntity): Boolean = sidedInventoryFixedWrapper.canPlayerUseInv(player)

    override fun getInvSize(): Int = sidedInventoryFixedWrapper.invSize

    override fun takeInvStack(slot: Int, amount: Int): ItemStack = sidedInventoryFixedWrapper.takeInvStack(slot, amount)

    override fun isInvEmpty(): Boolean = sidedInventoryFixedWrapper.isInvEmpty

    class AttributeInsertableSide(private val internal: ItemInsertable) : ItemInsertable {
        override fun attemptInsertion(stack: ItemStack, simulation: Simulation): ItemStack = internal.attemptInsertion(stack, simulation)
    }
    class AttributeExtractableSide(private val internal: ItemExtractable) : ItemExtractable {
        override fun attemptExtraction(filter: ItemFilter, maxAmount: Int, simulation: Simulation): ItemStack = internal.attemptExtraction(filter, maxAmount, simulation)
    }

    class AttributeTransferableSide(private val internal: ItemTransferable) : ItemTransferable {
        override fun attemptInsertion(stack: ItemStack, simulation: Simulation): ItemStack = internal.attemptInsertion(stack, simulation)
        override fun attemptExtraction(filter: ItemFilter, maxAmount: Int, simulation: Simulation): ItemStack = internal.attemptExtraction(filter, maxAmount, simulation)
    }

}