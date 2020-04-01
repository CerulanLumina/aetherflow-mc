package net.cerulan.luminality.api.client

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.block.Block

object PreviewRenderAPI {

    internal val needsPreviewSet: ObjectOpenHashSet<Block> = ObjectOpenHashSet()

    fun needsPreview(block: Block): Boolean = needsPreviewSet.contains(block)
    fun registerNeedsPreview(block: Block) {
        needsPreviewSet.add(block)
    }

}