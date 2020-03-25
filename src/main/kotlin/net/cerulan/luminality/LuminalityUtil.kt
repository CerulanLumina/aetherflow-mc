package net.cerulan.luminality

import com.google.common.collect.ImmutableSet
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs

object LuminalityUtil {

    fun getDirectionRightAngle(index: Int, direction: Direction): Direction {
        val i = index % 4
        val axis = Direction.Axis.values().filter { axis -> axis != direction.axis }[i % 2]
        return Direction.from(axis, Direction.AxisDirection.values()[i / 2])
    }

    fun getDirectionRightAngleIndex(input: Direction, output: Direction): Int {
        return dirToIndex[input]!![output]!!
    }

    private val dirToIndex: EnumMap<Direction, EnumMap<Direction, Int>> = EnumMap(Direction::class.java)

    init {
        for (inD in Direction.values()) {
            for (i in 0..3) {
                val outD = getDirectionRightAngle(i, inD)
                dirToIndex.compute(inD) { _,map ->
                    if (map == null) {
                        val nmap = EnumMap<Direction, Int>(Direction::class.java)
                        nmap[outD] = i
                        nmap
                    } else {
                        map[outD] = i
                        map
                    }
                }
            }
        }
    }

    private val topTri = arrayOf(Vec2f(0f, 0f), Vec2f(1f, 0f), Vec2f(0.5f, 0.5f))
    private val leftTri = arrayOf(Vec2f(0f, 0f), Vec2f(0f, 1f), Vec2f(0.5f, 0.5f))
    private val rightTri = arrayOf(Vec2f(1f, 0f), Vec2f(1f, 1f), Vec2f(0.5f, 0.5f))
    private val bottomTri = arrayOf(Vec2f(0f, 1f), Vec2f(1f, 1f), Vec2f(0.5f, 0.5f))
    private val triangles: Array<Array<Vec2f>> = arrayOf(topTri, leftTri, rightTri, bottomTri)

    private val yAxisDirs = arrayOf(Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH)
    private val xAxisDirs = arrayOf(Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.UP)
    private val zAxisDirs = arrayOf(Direction.DOWN, Direction.WEST, Direction.EAST, Direction.UP)

    /**
     * Gets the direction of the block clicked
     * @param hitSide The side that was hit
     * @param hitX Fractional x component of the block coordinate hit
     * @param hitY Fractional y component of the block coordinate hit
     * @param hitZ Fractional z component of the block coordinate hit
     */
    fun getDirectionFromHitPos(hitSide: Direction, hitX: Double, hitY: Double, hitZ: Double): Direction {
        return when (hitSide) {
            Direction.UP, Direction.DOWN -> {
                yAxisDirs[triangles.indexOfFirst { tri -> pointInTriangle(Vec2f(hitX.toFloat(), hitZ.toFloat()), tri) }]
            }
            Direction.WEST, Direction.EAST -> {
                xAxisDirs[triangles.indexOfFirst { tri -> pointInTriangle(Vec2f(hitZ.toFloat(), hitY.toFloat()), tri) }]
            }
            Direction.SOUTH, Direction.NORTH -> {
                zAxisDirs[triangles.indexOfFirst { tri -> pointInTriangle(Vec2f(hitX.toFloat(), hitY.toFloat()), tri) }]
            }
        }
    }

    private fun sign(points: Array<Vec2f>): Float {
        return (points[0].x - points[2].x) * (points[1].y - points[2].y) - (points[1].x - points[2].x) * (points[0].y - points[2].y)
    }

    fun pointInTriangle(point: Vec2f, triangle: Array<Vec2f>): Boolean {
        val d1 = sign(arrayOf(point, triangle[0], triangle[1]))
        val d2 = sign(arrayOf(point, triangle[1], triangle[2]))
        val d3 = sign(arrayOf(point, triangle[2], triangle[0]))
        val hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
        val hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)
        return !(hasNeg && hasPos)
    }


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

fun CompoundTag.putVec3d(key: String, vec3d: Vec3d) {
    val inner = CompoundTag()
    inner.putDouble("x", vec3d.x)
    inner.putDouble("y", vec3d.y)
    inner.putDouble("z", vec3d.z)
    this.put(key, inner)
}

fun CompoundTag.getVec3d(key: String): Vec3d? {
    val inner: CompoundTag? = if (this.contains("target")) { this.getCompound(key) } else { null }
    return inner?.let {
        val x = it.getDouble("x")
        val y = it.getDouble("y")
        val z = it.getDouble("z")
        Vec3d(x, y, z)
    }
}

fun Vec3i?.toVec3d(): Vec3d? {
    return this?.let {
        Vec3d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble())
    }
}
