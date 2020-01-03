package net.cerulan.luminality.networking

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry

object LuminalityPackets {

    fun registerC2S() {
        ServerSidePacketRegistry.INSTANCE.register(MachineConfigPacket.ID, MachineConfigPacket)
    }

}