package net.cerulan.luminality.client.render.ghosts

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.SpriteAtlasTexture
import org.lwjgl.opengl.GL11

class PlacementPreviewRenderLayer : RenderLayer("LUMUS_PLACEMENT_PREVIEW",
    VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
    GL11.GL_QUADS,
    1.shl(12),
    false,
    true,
    {
        texture.startDrawing()
        RenderSystem.enableBlend()
        RenderSystem.enableAlphaTest()
        RenderSystem.defaultAlphaFunc()
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.CONSTANT_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO)
        RenderSystem.blendColor(1f, 1f, 1f, 0.5f)
        GL11.glDepthRange(0.0, 0.0)
        RenderSystem.enableCull()
        RenderSystem.enableDepthTest()
    },
    {
        GL11.glDepthRange(0.0, 1.0)
        RenderSystem.blendColor(0f, 0f, 0f, 0f)
        RenderSystem.disableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableCull()
        texture.endDrawing()
    }) {
    companion object {
        val texture = Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, true)
    }
}