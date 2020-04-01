package net.cerulan.luminality.api

data class LumusPower(var radiance: Int, var flow: Int, var stability: Int = maximumStability) {

    companion object {
        const val maximumStability = 1
    }

    fun copy(other: LumusPower): Boolean {
        val ret = flow != other.flow || radiance != other.radiance || stability != other.stability
        other.flow = flow
        other.radiance = radiance
        other.stability = stability
        return ret
    }

    fun zero() {
        radiance = 0
        flow = 0
        stability = 0
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