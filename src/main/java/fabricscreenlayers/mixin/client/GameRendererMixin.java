package fabricscreenlayers.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import fabricscreenlayers.ScreenLayerManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fabricscreenlayers.ScreenLayerManager.SCREENS;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    private GuiGraphics graphics;

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexSorting;)V"))
    public void fabricscreenlayers_render4f(Matrix4f matrix4f, VertexSorting sorting)
    {
        RenderSystem.setProjectionMatrix(render4fTranslate(), sorting);
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    public void fabricscreenlayers_renderTranslate(PoseStack poseStack, float f, float g, float h)
    {
        renderTranslate(poseStack);
    }

    @ModifyVariable(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
                    shift = At.Shift.BEFORE))
    public GuiGraphics fabricscreenlayers_renderDrawScreenPre(GuiGraphics graphics)
    {

        this.graphics = graphics;
        drawScreen(graphics);
        return graphics;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
                    shift = At.Shift.AFTER))
    public void fabricscreenlayers_renderDrawScreenPost(float f, long l, boolean bl, CallbackInfo ci)
    {
        drawScreenPost(this.graphics.pose());
    }

    void renderTranslate(PoseStack poseStack)
    {
        poseStack.translate(0.0, 0.0, 1000.0 - ScreenLayerManager.getFarPlane());
    }

    void drawScreen(GuiGraphics graphics)
    {
        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        graphics.pose().pushPose();
        SCREENS.forEach(layer -> {
            layer.render(graphics, 0x7fffffff, 0x7fffffff, partialTick);
            graphics.pose().translate(0, 0, 2000);
        });
    }

    void drawScreenPost(PoseStack poseStack)
    {
        poseStack.popPose();
    }

    Matrix4f render4fTranslate()
    {
        Window window = Minecraft.getInstance().getWindow();
        return new Matrix4f()
                .ortho(0.0f,
                        (float) ((double) window.getWidth() / window.getGuiScale()),
                        (float) ((double) window.getHeight() / window.getGuiScale()),
                        0.0f,
                        1000.0f,
                        ScreenLayerManager.getFarPlane(),
                        FabricLoader.getInstance().isModLoaded("vulkanmod")); // GL needs false, Vulkan needs true. If mod is loaded, supply true.
    }
}
