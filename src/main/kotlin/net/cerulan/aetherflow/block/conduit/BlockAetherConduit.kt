package net.cerulan.aetherflow.block.conduit

import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.sound.BlockSoundGroup

abstract class BlockAetherConduit :
    Block(FabricBlockSettings.of(Material.STONE).breakByHand(true).strength(0.2f, 10f).sounds(BlockSoundGroup.GLASS).build()) {



}