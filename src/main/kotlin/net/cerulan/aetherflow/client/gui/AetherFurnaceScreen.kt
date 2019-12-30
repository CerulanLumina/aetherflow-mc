package net.cerulan.aetherflow.client.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.cerulan.aetherflow.container.AetherFurnaceController
import net.minecraft.entity.player.PlayerEntity

class AetherFurnaceScreen(container: AetherFurnaceController, player: PlayerEntity) : CottonInventoryScreen<AetherFurnaceController>(container, player)