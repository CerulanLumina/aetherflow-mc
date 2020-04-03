package net.cerulan.luminality.api

interface LumusPowerCopy {

    fun copyToOther(other: LumusPowerCopy): Boolean
    fun receiveCopyData(radiance: Int, flow: Int, stability: Int): Boolean

}