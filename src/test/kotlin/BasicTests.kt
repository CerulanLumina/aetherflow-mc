import net.cerulan.luminality.LuminalityUtil
import net.minecraft.util.math.Direction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object BasicTests {

    @Test
    fun rightAngleDirections() {
        assertEquals(Direction.EAST, LuminalityUtil.getDirectionRightAngle(0, Direction.NORTH))
        assertEquals(Direction.UP, LuminalityUtil.getDirectionRightAngle(1, Direction.NORTH))
        assertEquals(Direction.WEST, LuminalityUtil.getDirectionRightAngle(2, Direction.NORTH))
        assertEquals(Direction.DOWN, LuminalityUtil.getDirectionRightAngle(3, Direction.NORTH))

        assertEquals(Direction.EAST, LuminalityUtil.getDirectionRightAngle(0, Direction.SOUTH))
        assertEquals(Direction.UP, LuminalityUtil.getDirectionRightAngle(1, Direction.SOUTH))
        assertEquals(Direction.WEST, LuminalityUtil.getDirectionRightAngle(2, Direction.SOUTH))
        assertEquals(Direction.DOWN, LuminalityUtil.getDirectionRightAngle(3, Direction.SOUTH))
    }

}