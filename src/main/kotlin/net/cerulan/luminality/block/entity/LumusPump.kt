package net.cerulan.luminality.block.entity

import net.cerulan.luminality.LuminalityBlocks
import net.cerulan.luminality.api.LuminalityAttributes
import net.cerulan.luminality.api.client.BeamRenderBE
import net.cerulan.luminality.block.lumus.LumusPumpBlock
import net.cerulan.luminality.lumus.BeamHandler
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World

class LumusPump(
    blockEntityType: BlockEntityType<*> = LuminalityBlocks.BlockEntities.lumusPumpEntity) : BlockEntity(blockEntityType), Tickable,
    BlockEntityClientSerializable, BeamRenderBE {

    companion object {
        val range = 16
    }

    private val beamHandler = BeamHandler(this, null, range, pos, {
        world!!.setBlockState(pos, cachedState.with(LumusPumpBlock.Props.valid, it))
        sync()
    })

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

    private fun CompoundTag.putVec3d(key: String, vec3d: Vec3d) {
        val inner = CompoundTag()
        inner.putDouble("x", vec3d.x)
        inner.putDouble("y", vec3d.y)
        inner.putDouble("z", vec3d.z)
        this.put(key, inner)
    }

    private fun CompoundTag.getVec3d(key: String): Vec3d? {
        val inner: CompoundTag? = if (this.contains("target")) { this.getCompound(key) } else { null }
        return inner?.let {
            val x = it.getDouble("x")
            val y = it.getDouble("y")
            val z = it.getDouble("z")
            Vec3d(x, y, z)
        }
    }

    private fun Vec3i?.toVec3d(): Vec3d? {
        return this?.let {
            Vec3d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble())
        }
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        if (beamHandler.target.blockPos != null)
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