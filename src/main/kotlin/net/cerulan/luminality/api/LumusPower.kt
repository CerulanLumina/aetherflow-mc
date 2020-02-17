package net.cerulan.luminality.api

data class LumusPower(var radiance: Int, var flow: Int, var stability: Int = maximumStability) {

    companion object {
        const val maximumStability = 1
    }

    fun copy(other: LumusPower) {
        other.flow = flow
        other.radiance = radiance
        other.stability = stability
    }

    override fun equals(other: Any?): Boolean {
        return if (other is LumusPower) {
            other.radiance == radiance && other.flow == flow
        } else false
    }

    override fun hashCode(): Int {
        return radiance.shl(16) + flow
    }
}