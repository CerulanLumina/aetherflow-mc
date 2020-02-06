package net.cerulan.luminality.block.entity

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.LuminalityUtil
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusNodeMode
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

open class LumusRedirector : LumusPump(LuminalityBlocks.BlockEntities.lumusRedirectorEntity), Tickable,
    BlockEntityClientSerializable {

    val lumusSink = LumusNode(LumusNodeMode.SINK)
    val lumusSource = LumusNode(LumusNodeMode.SOURCE)

    init {
        lumusSink.attachRange = 0.1f
    }

    override fun tick() {
        lumusSource.radiance = lumusSink.radiance
        lumusSource.flow = lumusSink.flow
        super.tick()
    }

    override val outputDirection: Direction?
        get() {
            return direction?.let {
                LuminalityUtil.getDirectionRightAngle(cachedState[LumusRedirectorBlock.Props.output],
                    it
                )
            }
        }

    override fun getInputNode(world: World, pos: BlockPos, direction: Direction): LumusNode? {
        return if (lumusSource.radiance * lumusSource.flow > 0) lumusSource else null
    }

}