package net.cerulan.luminality.block.entity

import net.cerulan.luminality.*
import net.cerulan.luminality.api.attr.LumusSink
import net.cerulan.luminality.api.attr.LumusSource
import net.cerulan.luminality.api.client.BeamRenderBE
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.cerulan.luminality.lumus.BeamHandler
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LumusRedirector : BlockEntity(LuminalityBlocks.BlockEntities.lumusRedirectorEntity),
    Tickable,
    BlockEntityClientSerializable,
    BeamRenderBE {

    private val beamHandler = BeamHandler(this, null, LumusPump.range, pos, {
        world!!.setBlockState(pos, cachedState.with(LumusPumpBlock.Props.valid, it))
        sync()
    }, {sync()})

    private val redirectSource = LumusSource()
    val inputSink = LumusSink()

    init {
        beamHandler.inputNode = redirectSource
    }

    override fun setLocation(world: World?, pos: BlockPos) {
        super.setLocation(world, pos)
        if (world != null) {
            beamHandler.target.changeWorld(world)
            beamHandler.startBlockPos = pos
        }
    }

    override fun tick() {
        if (world!!.isClient) return
        if (inputSink.power.copy(redirectSource.power)) sync()
        val outDir = LuminalityUtil.getDirectionRightAngle(
            cachedState[LumusRedirectorBlock.Props.output],
            cachedState[LumusPumpBlock.Props.input]
        )
        if (beamHandler.direction !== outDir) {
            beamHandler.direction = outDir
        }
        beamHandler.tick()
    }

    fun onBroken() {
        beamHandler.target.cachedSink?.power?.zero()
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        if (beamHandler.target.blockPos != null && beamHandler.inputNode?.power?.radiance ?: 0 > 0)
            tag.putVec3d("target", beamHandler.target.blockPos!!.toVec3d()!!)
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        targetPos = tag.getVec3d("target")
    }

    override var targetPos: Vec3d? = null

    override val startPos: Vec3d?
        get() = pos.toVec3d()

}