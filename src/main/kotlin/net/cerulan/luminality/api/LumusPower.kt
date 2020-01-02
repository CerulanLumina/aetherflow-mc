package net.cerulan.luminality.api

data class LumusPower(val radiance: Int, val flow: Int) {
    override fun equals(other: Any?): Boolean {
        return if (other is LumusPower) {
            other.radiance == radiance && other.flow == flow
        } else false
    }

    override fun hashCode(): Int {
        return radiance.shl(16) + flow
    }
}