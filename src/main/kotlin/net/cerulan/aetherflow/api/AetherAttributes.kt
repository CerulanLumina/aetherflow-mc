package net.cerulan.aetherflow.api

import alexiil.mc.lib.attributes.Attributes
import net.cerulan.aetherflow.api.attr.AetherConduit
import net.cerulan.aetherflow.api.attr.AetherNode

object AetherAttributes {

    val AETHER_NODE = Attributes.create(AetherNode::class.java)
    val AETHER_CONDUIT = Attributes.create(AetherConduit::class.java)

}