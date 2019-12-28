package net.cerulan.aetherflow.api.attr

data class AetherConduit(val maxRadiance: Int) {
    companion object {
        val BASIC = AetherConduit(8)
    }
}