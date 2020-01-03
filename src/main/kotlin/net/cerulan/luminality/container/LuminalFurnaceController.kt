package net.cerulan.luminality.container

import io.github.cottonmc.cotton.gui.CottonCraftingController
import io.github.cottonmc.cotton.gui.widget.*
import io.netty.buffer.Unpooled
import net.cerulan.luminality.networking.MachineConfigPacket
import net.cerulan.luminality.recipe.LuminalityRecipeTypes
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class LuminalFurnaceController(syncId: Int, playerInv: PlayerInventory, context: BlockContext) :
    CottonCraftingController(
        LuminalityRecipeTypes.luminalFurnace,
        syncId,
        playerInv,
        getBlockInventory(context),
        getBlockPropertyDelegate(context)
    ) {

    companion object {
        private val bar_full = Identifier("luminality", "textures/gui/container/progress_full.png")
        private val bar_empty = Identifier("luminality", "textures/gui/container/progress_empty.png")
    }

    private val inputSlot: WItemSlot
    private val outputSlot: WItemSlot

    init {
        val rootPanel = getRootPanel() as WGridPanel
        rootPanel.add(WLabel(TranslatableText("block.luminality.luminal_furnace"), WLabel.DEFAULT_TEXT_COLOR), 0, 0)
        inputSlot = WItemSlot.of(blockInventory, 0)
        outputSlot = WItemSlot.of(blockInventory, 1)

        val modeSprite = WSpriteButton(bar_full, bar_empty)
        modeSprite.setSize(18, 18)
        modeSprite.currentImage = propertyDelegate.get(2)

        val progBar = WBar(bar_empty, bar_full, 0, 1, WBar.Direction.RIGHT)

        rootPanel.add(progBar, 3, 1)
        rootPanel.add(inputSlot, 2, 1)
        rootPanel.add(outputSlot, 6, 1)
        rootPanel.add(modeSprite, 0, 1)

        progBar.setLocation(inputSlot.absoluteX + 18, inputSlot.absoluteY + 1)
        progBar.setSize((3 * 18 - 2), (3 * 18 - 2) / 4)

        modeSprite.onClickRun =  { i -> run {
            propertyDelegate.set(2, i)
            modeSprite.currentImage += 1
            context.run { world: World, blockPos: BlockPos -> run {
                val packet = PacketByteBuf(Unpooled.buffer())
                packet.writeBlockPos(blockPos)
                packet.writeByte(0)
                val data = ByteArray(1)
                data[0] = modeSprite.currentImage.toByte()
                packet.writeByteArray(data)
                ClientSidePacketRegistry.INSTANCE.sendToServer(MachineConfigPacket.ID, packet)
            } }
        }}

        rootPanel.add(createPlayerInventoryPanel(), 0, 3)
        rootPanel.validate(this)
    }

    override fun getCraftingResultSlotIndex(): Int = -1
}