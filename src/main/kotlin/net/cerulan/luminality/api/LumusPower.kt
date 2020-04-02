package net.cerulan.luminality.api

class LumusPower(var radiance: Int, var flow: Int, var stability: Int = maximumStability) : LumusPowerCopy {

    companion object {
        const val maximumStability = 1
    }

    override fun receiveCopyData(radiance: Int, flow: Int, stability: Int): Boolean {
        val ret = radiance != this.radiance || flow != this.flow || stability != this.stability
        this.radiance = radiance
        this.flow = flow
        this.stability = stability
        return ret
    }

    override fun copyToOther(other: LumusPowerCopy): Boolean {
        return other.receiveCopyData(radiance, flow, stability)
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