package net.cerulan.luminality.networking

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import java.lang.Exception

object MachineConfigPacket : PacketConsumer {

    val ID = Identifier("luminality", "machine_config_packet")

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        val pos = buf.readBlockPos()
        val type = buf.readByte()
        val data = buf.readByteArray()
        ctx.taskQueue.execute {
            val be = ctx.player.world.getBlockEntity(pos)
            if (be is PacketConfigurable) {
                try {
                    val nBytes = be.expectedBytesForType(type)
                    if (data.size < nBytes) throw IllegalStateException("Not enough bytes for given type")
                    be.configureFromPacket(data)
                } catch (ex: Exception) {
                    println("Player requested a malformed packet! ${ctx.player}\n${ex}")
                }
            } else {
                println("Player requested to configure a block that isn't configurable! ${ctx.player}")
            }
        }
    }

}