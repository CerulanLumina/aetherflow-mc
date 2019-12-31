package net.cerulan.aetherflow.container

import io.github.cottonmc.cotton.gui.CottonCraftingController
import io.github.cottonmc.cotton.gui.widget.*
import net.cerulan.aetherflow.recipe.AetherflowRecipeTypes
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

class AetherFurnaceController(syncId: Int, playerInv: PlayerInventory, context: BlockContext): CottonCraftingController(AetherflowRecipeTypes.AETHER_FURNACE, syncId, playerInv, getBlockInventory(context), getBlockPropertyDelegate(context)) {

    companion object {
        private val bar_full = Identifier("aetherflow", "textures/gui/container/progress_full.png")
        private val bar_empty = Identifier("aetherflow", "textures/gui/container/progress_empty.png")
    }

    private val inputSlot: WItemSlot
    private val outputSlot: WItemSlot
    init {
        val rootPanel = getRootPanel() as WGridPanel
        rootPanel.add(WLabel(TranslatableText("block.aetherflow.aether_furnace"), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        inputSlot = WItemSlot.of(blockInventory, 0)
        outputSlot = WItemSlot.of(blockInventory, 1)

        val progBar = WBar(bar_empty, bar_full, 0, 1, WBar.Direction.RIGHT)

        rootPanel.add(progBar, 3, 1)
        rootPanel.add(inputSlot, 2, 1)
        rootPanel.add(outputSlot, 6, 1)

        progBar.setLocation(inputSlot.absoluteX + 18, inputSlot.absoluteY + 1)
        progBar.setSize((3*18-2), (3*18-2)/4)

        rootPanel.add(createPlayerInventoryPanel(), 0, 3)
        rootPanel.validate(this)
    }

    override fun getCraftingResultSlotIndex(): Int = -1
}