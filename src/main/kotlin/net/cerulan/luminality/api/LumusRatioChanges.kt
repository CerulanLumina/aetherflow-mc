package net.cerulan.luminality.api

enum class LumusRatioChanges(val radianceMult: Int, val radianceDiv: Int, val flowMult: Int, val flowDiv: Int) {
    INCREASE_RADIANCE(2, 1, 1, 2),
    INCREASE_FLOW(1, 2, 2, 1);

    fun getNewRadiance(inRadiance: Int) = inRadiance * radianceMult / radianceDiv
    fun getNewFlow(inFlow: Int) = inFlow * flowMult / flowDiv

}