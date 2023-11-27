package fabricscreenlayers.mixin.client;

import fabricscreenlayers.ScreenLayerManager;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin
{
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    public void fabricscreenlayers_onClose(CallbackInfo info)
    {
        if (ScreenLayerManager.popLayerInternal())
        {
            info.cancel();
        }
    }
}
