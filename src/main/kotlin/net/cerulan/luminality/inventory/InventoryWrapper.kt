package net.cerulan.luminality.inventory

import alexiil.mc.lib.attributes.item.FixedItemInv
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper
import alexiil.mc.lib.attributes.item.compat.SidedInventoryFixedWrapper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World


object InventoryWrapper {

    fun create(inv: FixedItemInv): SidedInventoryFixedWrapper {
        return object : SidedInventoryFixedWrapper(inv) {
            override fun canPlayerUseInv(player: PlayerEntity?): Boolean {
                return true
            }
        }
    }

    fun createRanged(inv: FixedItemInv, range: Int, pos: BlockPos, world: World): InventoryFixedWrapper {
        val be = world.getBlockEntity(pos)
        return object : InventoryFixedWrapper(inv) {
            override fun canPlayerUseInv(player: PlayerEntity): Boolean {
                return world.getBlockEntity(pos) == be && player.squaredDistanceTo(Vec3d(pos)) < range * range
            }
        }
    }

}