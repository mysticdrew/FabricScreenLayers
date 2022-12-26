package fabricscreenlayers;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.Stack;

public class ScreenLayerManager
{
    private static final Stack<Screen> SCREENS = new Stack<>();

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

    public static void resizeLayers(int width, int height)
    {
        Minecraft minecraft = Minecraft.getInstance();
        SCREENS.forEach(screen -> screen.resize(minecraft, width, height));
    }

    public static void clearLayers()
    {
        Minecraft minecraft = Minecraft.getInstance();
        while (SCREENS.size() > 0)
        {
            popLayer(minecraft);
        }
    }

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

    private static void popLayer(Minecraft minecraft)
    {
        if (minecraft.screen != null)
        {
            minecraft.screen.removed();
        }
        minecraft.screen = SCREENS.pop();
    }

    public static float getFarPlane()
    {
        return 1000.0F + 2000.0F * (1 + SCREENS.size());
    }

    public static void drawScreen(PoseStack poseStack)
    {
        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        poseStack.pushPose();
        SCREENS.forEach(layer -> {
            layer.render(poseStack, 0x7fffffff, 0x7fffffff, partialTick);
            poseStack.translate(0, 0, 2000);
        });

    }

    public static Matrix4f render4fTranslate()
    {
        Window window = Minecraft.getInstance().getWindow();
//        return new Matrix4f().setOrtho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, 3000.0f);

        return new Matrix4f().ortho(0.0f, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0f, 1000.0f, ScreenLayerManager.getFarPlane());
    }

    public static void renderTranslate(PoseStack poseStack)
    {
        poseStack.translate(0.0, 0.0, 1000.0 - ScreenLayerManager.getFarPlane());
    }

    public static void drawScreenPost(PoseStack poseStack)
    {
        poseStack.popPose();
    }
}
