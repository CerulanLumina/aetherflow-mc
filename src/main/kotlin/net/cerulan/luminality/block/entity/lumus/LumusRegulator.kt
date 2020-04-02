package net.cerulan.luminality.block.entity.lumus

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LumusRatioChanges
import net.cerulan.luminality.api.RatioLumusPower
import net.cerulan.luminality.api.attr.LumusSink
import net.cerulan.luminality.api.attr.LumusSource
import net.cerulan.luminality.api.client.BeamRenderBE
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
import net.cerulan.luminality.block.lumus.LumusRegulatorBlock
import net.cerulan.luminality.getVec3d
import net.cerulan.luminality.lumus.BeamHandler
import net.cerulan.luminality.putVec3d
import net.cerulan.luminality.toVec3d
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LumusRegulator : BlockEntity(LuminalityBlocks.BlockEntities.lumusRegulatorEntity),
    Tickable,
    BlockEntityClientSerializable,
    BeamRenderBE {

    val inputSink = LumusSink()
    val outputSource = LumusSource()

    private val beamHandler = BeamHandler(this, null, LumusPump.range, pos, {
        world!!.setBlockState(pos, cachedState.with(LumusPumpBlock.Props.valid, it))
        sync()
    }, {sync()})

    private val ratioPower = RatioLumusPower(0, 0, 0, LumusRatioChanges.INCREASE_FLOW, 2)

    val inputDirection: Direction
        get() = cachedState[LumusPumpBlock.Props.input]

    val outputDirection: Direction
        get() = inputDirection.opposite

    init {
        beamHandler.inputNode = outputSource
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
        if (inputSink.power.copyToOther(ratioPower)) sync()
        if (ratioPower.copyToOther(outputSource.power)) sync()
        if (beamHandler.direction !== outputDirection) {
            onBroken()
            beamHandler.direction = outputDirection
        }
        beamHandler.tick()
    }

    fun onBroken() {
        beamHandler.target.cachedSink?.power?.zero()
    }

    override fun fromTag(tag: CompoundTag) {
        ratioPower.mode = LumusRatioChanges.values()[tag.getInt("mode")]
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putInt("mode", ratioPower.mode.ordinal)
        return super.toTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        if (beamHandler.target.blockPos != null && beamHandler.inputNode?.power?.radiance ?: 0 > 0) {
            tag.putVec3d("target", beamHandler.target.blockPos!!.toVec3d()!!)
        }
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        targetPos = tag.getVec3d("target")
    }

    override var targetPos: Vec3d? = null
    override val startPos: Vec3d?
        get() = pos.toVec3d()

}