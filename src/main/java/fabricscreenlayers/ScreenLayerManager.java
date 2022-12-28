package fabricscreenlayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;
import java.util.Stack;

public class ScreenLayerManager
{
    public static final Stack<Screen> SCREENS = new Stack<>();

    /**
     * Adds a layered screen on top of the current screen.
     *
     * @param screen - the screen.
     */
    public static void pushLayer(Screen screen)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null)
        {
            SCREENS.push(minecraft.screen);
        }
        minecraft.screen = Objects.requireNonNull(screen);
        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.getNarrator().sayNow(screen.getNarrationMessage());
    }

    /**
     * Clears all screen layers.
     * Called automatically on {@link Minecraft#setScreen(Screen)}
     */
    public static void clearLayers()
    {
        Minecraft minecraft = Minecraft.getInstance();
        while (SCREENS.size() > 0)
        {
            popLayer(minecraft);
        }
    }

    /**
     * Removes the top most screen.
     */
    public static void popLayer()
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (SCREENS.size() == 0)
        {
            minecraft.setScreen(null);
            return;
        }

        popLayer(minecraft);
        if (minecraft.screen != null)
        {
            minecraft.getNarrator().sayNow(minecraft.screen.getNarrationMessage());
        }
    }

    /**
     * The Z coordinate for drawing a new screen layer.
     *
     * @return - The Z value.
     */
    public static float getFarPlane()
    {
        return 3000.0F * (1 + getScreenLayerCount());
    }


    /**
     * Gets the current layer count.
     *
     * @return - The count.
     */
    public static int getScreenLayerCount()
    {
        return SCREENS.size();
    }

    private static void popLayer(Minecraft minecraft)
    {
        if (minecraft.screen != null)
        {
            minecraft.screen.removed();
        }
        minecraft.screen = SCREENS.pop();
    }


}
