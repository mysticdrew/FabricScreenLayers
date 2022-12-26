package fabricscreenlayers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fabricscreenlayers.ScreenLayerManager;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    private PoseStack stack;

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;)V"))
    public void fabricscreenlayers_render4f(Matrix4f matrix4f)
    {
        RenderSystem.setProjectionMatrix(ScreenLayerManager.render4fTranslate());
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    public void fabricscreenlayers_renderTranslate(PoseStack poseStack, float f, float g, float h)
    {
        ScreenLayerManager.renderTranslate(poseStack);
    }

    @ModifyVariable(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                    shift = At.Shift.BEFORE),
            ordinal = 1)
    public PoseStack fabricscreenlayers_renderDrawScreenPre(PoseStack stack)
    {

        this.stack = stack;
        ScreenLayerManager.drawScreen(stack);
        return stack;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                    shift = At.Shift.AFTER))
    public void fabricscreenlayers_renderDrawScreenPost(float f, long l, boolean bl, CallbackInfo ci)
    {
        ScreenLayerManager.drawScreenPost(this.stack);
    }
}
