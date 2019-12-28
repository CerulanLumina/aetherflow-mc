package net.cerulan.aetherflow.api.attr

import com.google.common.collect.ImmutableSet
import net.minecraft.util.math.Direction

class AetherNode (_mode: AetherNodeMode, vararg _sides: Direction) {

    var radiance: Int = 0
    var flow: Int = 0
    val mode: AetherNodeMode = _mode
    var sides: ImmutableSet<Direction> = ImmutableSet.copyOf(_sides)

}