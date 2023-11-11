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
            minecraft.screen = Objects.requireNonNull(screen);
            screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
            minecraft.getNarrator().sayNow(screen.getNarrationMessage());
        }
        else
        {
            minecraft.setScreen(screen);
        }
    }

    /**
     * Clears all screen layers.
     * Called automatically on {@link Minecraft#setScreen(Screen)}
     */
    public static void clearLayers()
    {
        Minecraft minecraft = Minecraft.getInstance();
        while (!SCREENS.isEmpty())
        {
            popLayer(minecraft);
        }
    }

    /**
     * Removes the top most screen.
     */
    public static boolean popLayer()
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (SCREENS.isEmpty())
        {
            minecraft.setScreen(null);
            return false;
        }

        popLayer(minecraft);
        if (minecraft.screen != null)
        {
            minecraft.getNarrator().sayNow(minecraft.screen.getNarrationMessage());
            return true;
        }
        return false;
    }

    /**
     * The Z coordinate for drawing a new screen layer.
     *
     * @return - The Z value.
     */
    public static float getFarPlane()
    {
        return 1000.0F + 10000.0F * (1 + getScreenLayerCount());
    }


    /**
     * Returns the current layers on top of the bottom screen. The first screen is not a layer.
     * This means, if only one screen is displayed it will return 0.
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
