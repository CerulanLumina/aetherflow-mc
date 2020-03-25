package net.cerulan.luminality.api.client.render

import net.cerulan.luminality.client.render.beam.GenericBeamBERenderer
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.util.math.Vec3d

class DefaultBeamBERenderer<T: BlockEntity>(dispatcher: BlockEntityRenderDispatcher,
                                            startSupplier: (T) -> Vec3d?,
                                            targetSupplier: (T) -> Vec3d?
) : GenericBeamBERenderer<T>(dispatcher, startSupplier, targetSupplier)