package net.cerulan.luminality.lumus

import alexiil.mc.lib.attributes.SearchOptions
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.attr.LumusSource
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*
import kotlin.properties.Delegates

open class BeamHandler(
    open val parent: BlockEntity,
    open var inputNode: LumusSource?,
    open var range: Int,
    open var startBlockPos: BlockPos,
    open var onValidChange: (Boolean) -> Unit,
    open var syncCallback: () -> Unit,
    defaultDir: Direction? = null
) {

    protected open var cachedPosMap: EnumMap<Direction, Array<BlockPos>> = EnumMap(Direction::class.java)

    protected open fun generateCachedPosMap() {
        Direction.values().forEach { dir ->
            cachedPosMap[dir] = Array(range) {
                startBlockPos.offset(dir, it + 1)
            }
        }
    }

    open var direction: Direction? by Delegates.observable(defaultDir) { _, _, _ ->
        refreshCachedPos()
    }

    open val sinkInsertDirection
        get() = direction?.opposite

    protected open var cachedPos: Array<BlockPos>? = null

    protected open fun refreshCachedPos() {
        if (!hasCacheMap) generateCachedPosMap()
        cachedPos = if (direction == null) null
        else cachedPosMap[direction]!!
    }

    open var active: Boolean by Delegates.observable(false) { _, _, newValue -> onValidChange(newValue) }
        protected set

    open val target: BeamTarget = BeamTarget()

    private var hasCacheMap = false

    open fun tick() {
        // We are already active, inputNode is still nonnull, and we have a cached sink
        if (active && inputNode != null && target.cachedSink != null) {
            val sink = LuminalityAttributes.lumusSink.getFirstOrNull(target.world!!, target.blockPos!!, SearchOptions.inDirection(sinkInsertDirection))
            if (target.cachedSink != sink)
                target.blockPos = target.blockPos // refresh cachedSink

            if (sink != null && cachedPos?.first { pos -> !target.world!!.getBlockState(pos).isAir } == target.blockPos) {
                if (inputNode!!.power.copy(sink.power)) syncCallback()
            } else {
                unsetTarget()
            }

        } else if (inputNode != null && target.blockPos == null && target.world != null) {
            // We at least have an input node, but we have no target yet
            val firstNonAir = cachedPos?.firstOrNull { searchPos -> !target.world!!.getBlockState(searchPos).isAir }
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

    protected open fun unsetTarget() {
        active = false
        target.blockPos = null
    }

}