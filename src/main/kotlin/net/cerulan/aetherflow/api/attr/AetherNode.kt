package net.cerulan.aetherflow.api.attr

import net.minecraft.util.math.Direction

class AetherNode (_mode: AetherNodeMode, vararg _sides: Direction) {

    var radiance: Int = 0
    private var flow: Int = 0
    private val mode: AetherNodeMode = _mode
    var sides: HashSet<Direction> = HashSet()

    init {
        _sides.iterator().forEach { v -> sides.add(v) }
    }

}