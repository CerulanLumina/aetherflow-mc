package net.cerulan.luminality.api

import alexiil.mc.lib.attributes.Attribute
import alexiil.mc.lib.attributes.Attributes
import net.cerulan.luminality.api.attr.LumusPumpMarker
import net.cerulan.luminality.api.attr.LumusSink
import net.cerulan.luminality.api.attr.LumusSource

object LuminalityAttributes {

    val lumusSource: Attribute<LumusSource> = Attributes.create(LumusSource::class.java)
    val lumusSink: Attribute<LumusSink> = Attributes.create(LumusSink::class.java)
    val lumusPump: Attribute<LumusPumpMarker> = Attributes.create(LumusPumpMarker::class.java)

}