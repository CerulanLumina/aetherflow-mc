package net.cerulan.luminality.api

class RatioLumusPower(var radiance: Int, var flow: Int, var stability: Int, var mode: LumusRatioChanges, var factor: Int) : LumusPowerCopy {

    override fun copyToOther(other: LumusPowerCopy): Boolean {
        return if (mode == LumusRatioChanges.INCREASE_FLOW) {
            other.receiveCopyData(radiance / factor, flow * factor, stability)
        } else {
            other.receiveCopyData(radiance * factor, flow / factor, stability)
        }
    }

    override fun receiveCopyData(radiance: Int, flow: Int, stability: Int): Boolean {
        val ret = radiance != this.radiance || flow != this.flow || stability != this.stability
        this.radiance = radiance
        this.flow = flow
        this.stability = stability
        return ret
    }


}