package fabricscreenlayers.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fabricscreenlayers.ScreenLayerManager;
import net.minecraft.client.Minecraft;
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
    private PoseStack stack;

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;)V"))
    public void fabricscreenlayers_render4f(Matrix4f matrix4f)
    {
        RenderSystem.setProjectionMatrix(render4fTranslate());
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
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                    shift = At.Shift.BEFORE),
            ordinal = 1)
    public PoseStack fabricscreenlayers_renderDrawScreenPre(PoseStack poseStack)
    {

        this.stack = poseStack;
        drawScreen(stack);
        return stack;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                    shift = At.Shift.AFTER))
    public void fabricscreenlayers_renderDrawScreenPost(float f, long l, boolean bl, CallbackInfo ci)
    {
        drawScreenPost(this.stack);
    }

    void renderTranslate(PoseStack poseStack)
    {
        poseStack.translate(0.0, 0.0, 1000.0 - ScreenLayerManager.getFarPlane());
    }

    void drawScreen(PoseStack poseStack)
    {
        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        poseStack.pushPose();
        SCREENS.forEach(layer -> {
            layer.render(poseStack, 0x7fffffff, 0x7fffffff, partialTick);
            poseStack.translate(0, 0, 2000);
        });
    }

    void drawScreenPost(PoseStack poseStack)
    {
        poseStack.popPose();
    }

    Matrix4f render4fTranslate()
    {
        Window window = Minecraft.getInstance().getWindow();
        return new Matrix4f().ortho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, ScreenLayerManager.getFarPlane());
    }
}
