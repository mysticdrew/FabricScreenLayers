package example.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import fabricscreenlayers.ScreenLayerManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

public class ExampleScreen extends Screen
{
    private final String screenNumberLabel;
    private final int screenNumber;

    private Button addScreenButton;
    private Button closeScreenButton;
    private Button closeAllScreensButton;

    public ExampleScreen(int count)
    {
        super(Component.literal("Example Screen: " + count));
        this.screenNumber = count;
        this.screenNumberLabel = Integer.toString(screenNumber);
    }

    @Override
    public void init()
    {

        addScreenButton = Button.builder(Component.literal("Add Screen"), (button) ->
                ScreenLayerManager.pushLayer(new ExampleScreen(ScreenLayerManager.getScreenLayerCount() + 1))).build();

        closeScreenButton = Button.builder(Component.literal("Close Screen"), (button) -> ScreenLayerManager.popLayer()).build();

        closeAllScreensButton = Button.builder(Component.literal("Clear All Layers"), (button) -> ScreenLayerManager.clearLayers()).build();

        this.addRenderableWidget(positionWidget(addScreenButton, 0, this.height - 40));
        this.addRenderableWidget(positionWidget(closeScreenButton, (this.width / 2) - 50, this.height - 40));
        this.addRenderableWidget(positionWidget(closeAllScreensButton, this.width - 100, this.height - 40));

    }

    private Button positionWidget(Button button, int x, int y)
    {
        button.setX(x);
        button.setY(y);
        button.setWidth(100);
        return button;
    }

    @Override
    public void render(PoseStack poseStack, int x, int y, float partialTicks)
    {
        KeyMapping.releaseAll();
        super.renderBackground(poseStack);
        int xLoc = screenNumber == 0 ? 40 : 40 * screenNumber;

        // calling size display here prevents the label and box from being affected by minecraft's gui scale video setting
        sizeDisplay(minecraft.getWindow().getScreenWidth(), minecraft.getWindow().getScreenHeight());
        drawLabel(poseStack, screenNumberLabel, xLoc, 0, 5);
        sizeDisplay(width, height);

        if (this.screenNumber == ScreenLayerManager.getScreenLayerCount() || ScreenLayerManager.getScreenLayerCount() == 0)
        {
            super.render(poseStack, x, y, partialTicks);
        }
    }

    public static void drawLabel(PoseStack poseStack, final String text, double x, double y, double fontScale)
    {

        final Font fontRenderer = Minecraft.getInstance().font;

        final double width = fontRenderer.width(text);
        int height = fontRenderer.lineHeight + (fontRenderer.isBidirectional() ? 0 : 6);
        poseStack.pushPose();

        try
        {
            if (fontScale != 1)
            {
                x = x / fontScale;
                y = y / fontScale;
                poseStack.scale((float) fontScale, (float) fontScale, 1);
            }

            float textX = (float) x;
            float textY = (float) y;
            double rectX = x;
            double rectY = y;

            textX = (float) (x - (width / 2) + (fontScale > 1 ? .5 : 0));
            rectX = (float) (x - (Math.max(1, width) / 2) + (fontScale > 1 ? .5 : 0));

            rectY = y;
            textY = (float) (rectY + (height - fontRenderer.lineHeight) / 2.0);
            // Draw background
            drawRectangle(poseStack, (float) (rectX - 2 - .5), (float) rectY, (float) ((float) (width + (2 * 2))), height);
            // Font renderer really doesn't like mid-pixel text rendering
            poseStack.translate(textX - Math.floor(textX), textY - Math.floor(textY), 0);
            fontRenderer.drawShadow(poseStack, text, textX, textY, 0xFFFFFF);
        }
        finally
        {
            poseStack.popPose();
        }
    }

    public static void drawRectangle(PoseStack poseStack, float x, float y, float width, double height)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix4f = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x, (float) height + y, (float) 0).color(0xFF000000).endVertex();
        bufferBuilder.vertex(matrix4f, x + width, (float) (height + y), (float) 0).color(0xFF000000).endVertex();
        bufferBuilder.vertex(matrix4f, x + width, y, 0).color(0xFF000000).endVertex();
        bufferBuilder.vertex(matrix4f, x, y, 0).color(0xFF000000).endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    public void sizeDisplay(double width, double height)
    {
        if (this.width > 0 && this.height > 0)
        {
            RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 100.0F, ScreenLayerManager.getFarPlane());
            RenderSystem.setProjectionMatrix(matrix4f);
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.setIdentity();
            posestack.translate(0.0D, 0.0D, 1000.0F - ScreenLayerManager.getFarPlane());
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return true;
    }
}
