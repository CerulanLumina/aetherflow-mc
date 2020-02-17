package net.cerulan.luminality.lumus

import alexiil.mc.lib.attributes.SearchOptions
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.attr.LumusSource
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.properties.Delegates

open class BeamHandler(
    val parent: BlockEntity,
    var inputNode: LumusSource?,
    var range: Int,
    var startBlockPos: BlockPos,
    var onValidChange: (Boolean) -> Unit,
    defaultDir: Direction? = null
) {

    var direction: Direction? by Delegates.observable(defaultDir) { _, _, _ ->
        refreshCachedPos()
    }

    open val sinkInsertDirection = direction?.opposite

    protected open val cachedPos = ArrayList<BlockPos>(range)

    protected open fun refreshCachedPos() {
        if (direction == null) return
        cachedPos.clear()
        (0..range).forEach {
            cachedPos.add(startBlockPos.offset(direction, it + 1))
        }
    }

    private var active: Boolean by Delegates.observable(false) { _, _, newValue -> onValidChange(newValue) }

    val target: BeamTarget = BeamTarget()

    open fun tick() {
        // We are already active, inputNode is still nonnull, and we have a cached sink
        if (active && inputNode != null && target.cachedSink != null) {
            val sink = LuminalityAttributes.lumusSink.getFirstOrNull(target.world!!, target.blockPos!!)
            if (target.cachedSink != sink)
                target.blockPos = target.blockPos // refresh cachedSink

            if (sink != null && cachedPos.first { pos -> !target.world!!.getBlockState(pos).isAir } == target.blockPos) {
                inputNode!!.power.copy(sink.power)
            } else {
                unsetTarget()
            }

        } else if (inputNode != null && target.blockPos == null && target.world != null) {
            // We at least have an input node, but we have no target yet
            val firstNonAir = cachedPos.firstOrNull { searchPos -> !target.world!!.getBlockState(searchPos).isAir }
            val attr = firstNonAir?.let {
                LuminalityAttributes.lumusSink.getFirstOrNull(
                    target.world!!,
                    it,
                    SearchOptions.inDirection(sinkInsertDirection)
                )
            }
            if (attr != null) {
                target.blockPos = firstNonAir
                target.cachedSink = attr
                active = true
            }
        } else {
            if (active) active = false
            if (target.blockPos != null) {
                unsetTarget()
            }
        }
    }

    private fun unsetTarget() {
        active = false
        target.blockPos = null
    }

}