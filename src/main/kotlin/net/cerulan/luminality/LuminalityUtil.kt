package net.cerulan.luminality

import com.google.common.collect.ImmutableSet
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.abs

object LuminalityUtil {

    fun blockPosDFS(start: BlockPos, predicate: (BlockPos) -> Boolean): ImmutableSet<BlockPos> {
        val visited: HashSet<BlockPos> = HashSet()
        val stack = Stack<BlockPos>()
        stack.push(start)
        while (!stack.empty()) {
            val s = stack.pop()
            if (!visited.contains(s)) {
                visited.add(s)
            }
            getSurroundingPos(s).filter(predicate).filter{ bp -> !visited.contains(bp) } .forEach { bp -> stack.push(bp) }
        }
        return ImmutableSet.copyOf(visited)
    }

    fun getSurroundingPos(pos: BlockPos): Array<BlockPos> {
        return Array(6) { i -> when (i) {
            0 -> pos.down()
            1 -> pos.up()
            2 -> pos.north()
            3 -> pos.south()
            4 -> pos.west()
            5 -> pos.east()
            else -> pos
        } }
    }

    fun getDirectionPos(from: BlockPos, to: BlockPos): Direction? {
        val xd = from.x - to.x
        val yd = from.y - to.y
        val zd = from.z - to.z
        if (abs(xd) + abs(yd) + abs(zd) != 1) return null
        return if (abs(xd) > abs(yd) && abs(xd) > abs(zd)) {
            if (xd < 0) Direction.EAST
            else Direction.WEST
        } else if (abs(yd) > abs(xd) && abs(yd) > abs(zd)) {
            if (yd < 0) Direction.UP
            else Direction.DOWN
        }
        else if (abs(zd) > abs(xd) && abs(zd) > abs(yd)) {
            if (zd < 0) Direction.NORTH
            else Direction.SOUTH
        } else null
    }



}