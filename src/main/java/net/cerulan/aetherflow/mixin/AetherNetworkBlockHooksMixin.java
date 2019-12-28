package net.cerulan.aetherflow.mixin;

import alexiil.mc.lib.attributes.SearchOptions;
import net.cerulan.aetherflow.api.AetherAttributes;
import net.cerulan.aetherflow.api.AetherNetworks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Block.class)
public abstract class AetherNetworkBlockHooksMixin {
    @Inject(at = @At("RETURN"), method = "onBlockAdded")
    private void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved, CallbackInfo info) {
        if (world.isClient) return;
        Arrays.stream(Direction.values()).filter((dir) -> AetherAttributes.INSTANCE.getAETHER_NODE().getFirstOrNull(world, pos, SearchOptions.inDirection(dir)) != null)
                .findFirst().ifPresent((_dir) -> net.cerulan.aetherflow.event.AetherNetworkHooks.INSTANCE.addNode(world, pos));
        if (AetherAttributes.INSTANCE.getAETHER_CONDUIT().getFirstOrNull(world, pos) != null)
            net.cerulan.aetherflow.event.AetherNetworkHooks.INSTANCE.addConduit(world, pos);
    }

    @Inject(at = @At("RETURN"), method = "onBlockRemoved")
    private void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        AetherNetworks.INSTANCE.removeBlockFromNetwork(world, pos);
    }
}
