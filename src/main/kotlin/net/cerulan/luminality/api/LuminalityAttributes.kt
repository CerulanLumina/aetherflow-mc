package net.cerulan.luminality.api

import alexiil.mc.lib.attributes.Attribute
import alexiil.mc.lib.attributes.Attributes
import net.cerulan.luminality.api.attr.LumusNode
import net.cerulan.luminality.api.attr.LumusPumpMarker

object LuminalityAttributes {

    val lumusNode: Attribute<LumusNode> = Attributes.create(LumusNode::class.java)
    val lumusPump: Attribute<LumusPumpMarker> = Attributes.create(LumusPumpMarker::class.java)

}