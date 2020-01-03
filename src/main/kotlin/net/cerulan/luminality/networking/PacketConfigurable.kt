package net.cerulan.luminality.networking

interface PacketConfigurable {

    fun configureFromPacket(byteArray: ByteArray)
    fun expectedBytesForType(type: Byte): Int

}