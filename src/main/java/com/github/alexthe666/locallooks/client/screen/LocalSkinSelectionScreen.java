package com.github.alexthe666.locallooks.client.screen;

import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraftforge.client.event.ScreenEvent;

import java.io.File;

public class LocalSkinSelectionScreen extends Screen {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("locallooks:textures/gui/background.png");
    private static final Component TITLE_TEXT = Component.translatable("gui.locallooks.local_skin_selection");
    private final boolean offhand;
    private SkinListWidget list;
    private Button selectSkinBtn;

    protected LocalSkinSelectionScreen(boolean offhand) {
        super(TITLE_TEXT);
        this.offhand = offhand;
    }

    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
            this.minecraft.setScreen(new LookCustomizationScreen(offhand));
        }).size(120, 20).pos(this.width / 2 - 200, this.height - 48).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.locallooks.open_folder"), (button) -> {
            File path = SkinLoader.getSkinFolder();
            if (path != null) {
                Util.getPlatform().openFile(path);
            }
            this.list.repopulate();
        }).size(120, 20).pos(this.width / 2 - 60, this.height - 48).build());
        this.addRenderableWidget(selectSkinBtn = Button.builder(Component.translatable("gui.locallooks.select"), (button) -> {
            this.minecraft.setScreen(new LookCustomizationScreen(offhand, this.list.getSelected().getFile()));
        }).size(120, 20).pos(this.width / 2 + 80, this.height - 48).build());

        this.selectSkinBtn.active = false;
        this.list = new SkinListWidget(minecraft, this.width, this.height, TITLE_TEXT);
        this.addWidget(this.list);
    }

    @Override
    public void tick(){
        super.tick();
        this.selectSkinBtn.active = this.list.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(new LookCustomizationScreen(offhand));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.list.mouseScrolled(mouseX, mouseY, delta);
    }


    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderGlassBackground(guiGraphics, 0);
        this.list.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void renderGlassBackground(GuiGraphics guiGraphics, int vOffset) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        tesselator.end();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, guiGraphics));
    }
}
