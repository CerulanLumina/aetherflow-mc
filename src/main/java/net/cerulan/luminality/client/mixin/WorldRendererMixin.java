package net.cerulan.luminality.client.mixin;

import net.cerulan.luminality.client.render.ghosts.PlacementPreviewRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;"
            + "Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;"
            + "Lnet/minecraft/client/util/math/Matrix4f;)V",
    at = @At(value = "CONSTANT", args = "stringValue=particles"))
    private void renderDetached(MatrixStack matrixStack, float tickDelta, long startTime,
                                boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
                                CallbackInfo callbackInfo) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            PlacementPreviewRenderer.INSTANCE.render(matrixStack, camera, player);
        }


    }

}
