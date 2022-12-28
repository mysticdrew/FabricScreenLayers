package example.mod;

import com.mojang.blaze3d.platform.InputConstants;
import fabricscreenlayers.ScreenLayerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ClientModInitializer
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    KeyMapping keyMapping = new KeyMapping("examplemod.key.newscreen.label",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "examplemod.key.category");

    @Override
    public void onInitializeClient()
    {
        KeyBindingHelper.registerKeyBinding(keyMapping);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyMapping.consumeClick())
            {
                ScreenLayerManager.pushLayer(new ExampleScreen(ScreenLayerManager.getScreenLayerCount()));
            }
        });

        ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) ->
                ScreenKeyboardEvents.allowKeyPress(screen).register((keyScreen, key, scancode, modifiers) -> {
                    if (keyScreen != null)
                    {
                        if (keyMapping.getDefaultKey().getValue() == key)
                        {
                            ScreenLayerManager.pushLayer(new ExampleScreen(ScreenLayerManager.getScreenLayerCount() + 1));
                        }
                    }
                    return true;
                })));
    }
}
