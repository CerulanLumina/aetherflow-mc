package net.cerulan.aetherflow.api

data class AetherPower(val radiance: Int, val flow: Int) {
    override fun equals(other: Any?): Boolean {
        return if (other is AetherPower) {
            other.radiance == radiance && other.flow == flow
        } else false
    }

    override fun hashCode(): Int {
        return radiance.shl(16) + flow
    }
}