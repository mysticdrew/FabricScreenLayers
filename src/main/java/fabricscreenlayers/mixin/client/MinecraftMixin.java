package fabricscreenlayers.mixin.client;

import fabricscreenlayers.ScreenLayerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fabricscreenlayers.ScreenLayerManager.SCREENS;
import static org.objectweb.asm.Opcodes.PUTFIELD;


@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Shadow
    public ClientLevel level;

    @Inject(method = "resizeDisplay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;resize(Lnet/minecraft/client/Minecraft;II)V", shift = At.Shift.AFTER))
    public void fabricscreenlayers_resizeDisplay(CallbackInfo info)
    {
        Minecraft minecraft = Minecraft.getInstance();

        resizeLayers(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = PUTFIELD, shift = At.Shift.BEFORE))
    public void fabricscreenlayers_setScreen(Screen screen, CallbackInfo info)
    {
        ScreenLayerManager.clearLayers();
    }

    void resizeLayers(int width, int height)
    {
        Minecraft minecraft = Minecraft.getInstance();
        SCREENS.forEach(screen -> screen.resize(minecraft, width, height));
    }
}
