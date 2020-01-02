package net.cerulan.luminality.client.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.cerulan.luminality.container.LuminalFurnaceController
import net.minecraft.entity.player.PlayerEntity

class LuminalFurnaceScreen(container: LuminalFurnaceController, player: PlayerEntity) : CottonInventoryScreen<LuminalFurnaceController>(container, player)