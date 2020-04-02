package net.cerulan.luminality.block.entity.lumus

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.attr.LumusSink
import net.cerulan.luminality.api.attr.LumusSource
import net.cerulan.luminality.api.client.BeamRenderBE
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.block.lumus.LumusRedirectorBlock
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

    var sidesInverted: Boolean = false
        private set

    val outputDirection: Direction
        get() = if (!sidesInverted) { cachedState[LumusRedirectorBlock.Props.side2] } else { cachedState[LumusRedirectorBlock.Props.side1] }

    val inputDirection: Direction
        get() = if (!sidesInverted) { cachedState[LumusRedirectorBlock.Props.side1] } else { cachedState[LumusRedirectorBlock.Props.side2] }

    override fun setLocation(world: World?, pos: BlockPos) {
        super.setLocation(world, pos)
        if (world != null) {
            beamHandler.target.changeWorld(world)
            beamHandler.startBlockPos = pos
        }
    }

    override fun tick() {
        if (world!!.isClient) return
        if (inputSink.power.copyToOther(redirectSource.power)) sync()
        if (beamHandler.direction !== outputDirection) {
            onBroken()
            beamHandler.direction = outputDirection
        }
        beamHandler.tick()
        if (!beamHandler.active) {
            sidesInverted = !sidesInverted
        }
    }

    fun onBroken() {
        beamHandler.target.cachedSink?.power?.zero()
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putBoolean("SidesInverted", sidesInverted)
        return super.toTag(tag)
    }

    override fun fromTag(tag: CompoundTag) {
        sidesInverted = tag.getBoolean("SidesInverted")
        super.fromTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putBoolean("sidesInverted", sidesInverted)
        if (beamHandler.target.blockPos != null && beamHandler.inputNode?.power?.radiance ?: 0 > 0) {
            tag.putVec3d("target", beamHandler.target.blockPos!!.toVec3d()!!)
        }
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        sidesInverted = tag.getBoolean("sidesInverted")
        targetPos = tag.getVec3d("target")
    }

    override var targetPos: Vec3d? = null

    override val startPos: Vec3d?
        get() = pos.toVec3d()

}