package net.cerulan.aetherflow.mixin;

import alexiil.mc.lib.attributes.AttributeList;
import net.cerulan.aetherflow.api.AetherAttributes;
import net.cerulan.aetherflow.api.attr.AetherNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
// Mixins HAVE to be written in java due to constraints in the mixin system.
public abstract class AetherNodeBlockMixin {
    @Inject(at = @At("RETURN"), method = "onPlaced")
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo info) {
        if (world.isClient) return;
        AttributeList<AetherNode> list = AetherAttributes.INSTANCE.getAETHER_NODE().getAll(world, pos);
        if (list.getCount() > 0) {


        }
    }
}
