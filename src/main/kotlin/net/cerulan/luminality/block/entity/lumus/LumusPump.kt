package net.cerulan.luminality.block.entity.lumus

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.client.BeamRenderBE
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.getVec3d
import net.cerulan.luminality.lumus.BeamHandler
import net.cerulan.luminality.putVec3d
import net.cerulan.luminality.toVec3d
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LumusPump(
    blockEntityType: BlockEntityType<*> = LuminalityBlocks.BlockEntities.lumusPumpEntity) : BlockEntity(blockEntityType), Tickable,
    BlockEntityClientSerializable, BeamRenderBE {

    companion object {
        val range = 16
    }

    private val beamHandler = BeamHandler(this, null,
        range, pos, {
        world!!.setBlockState(pos, cachedState.with(LumusPumpBlock.Props.valid, it))
        sync()
    }, {sync()})

    override fun setLocation(world: World?, pos: BlockPos) {
        super.setLocation(world, pos)
        if (world != null) {
            beamHandler.target.changeWorld(world)
            beamHandler.startBlockPos = pos
        }
    }

    override fun tick() {
        if (world!!.isClient) return
        if (beamHandler.direction !== cachedState[LumusPumpBlock.Props.input].opposite) {
            beamHandler.direction = cachedState[LumusPumpBlock.Props.input].opposite
        }
        beamHandler.inputNode = LuminalityAttributes.lumusSource.getFirstOrNull(world!!, pos.offset(cachedState[LumusPumpBlock.Props.input]))
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