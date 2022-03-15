package com.github.alexthe666.locallooks.client.screen;

import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.File;

public class LocalSkinSelectionScreen extends Screen {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("locallooks:textures/gui/background.png");
    private static final ITextComponent TITLE_TEXT = new TranslationTextComponent("gui.locallooks.local_skin_selection");
    private final boolean offhand;
    private SkinListWidget list;
    private Button selectSkinBtn;

    protected LocalSkinSelectionScreen(boolean offhand) {
        super(TITLE_TEXT);
        this.offhand = offhand;
    }

    protected void init() {
        this.addButton(new Button(this.width / 2 - 200, this.height - 48, 120, 20, DialogTexts.GUI_CANCEL, (p_238903_1_) -> {
            this.minecraft.displayGuiScreen(new LookCustomizationScreen(offhand));
        }));
        this.addButton(new Button(this.width / 2 - 60, this.height - 48, 120, 20, new TranslationTextComponent("gui.locallooks.open_folder"), (p_238896_1_) -> {
            File path = SkinLoader.getSkinFolder();
            if (path != null) {
                Util.getOSType().openFile(path);
            }
            this.list.repopulate();
        }));
        this.addButton(selectSkinBtn = new Button(this.width / 2 + 80, this.height - 48, 120, 20, new TranslationTextComponent("gui.locallooks.select"), (p_238903_1_) -> {
            this.minecraft.displayGuiScreen(new LookCustomizationScreen(offhand, this.list.getSelected().getFile()));
        }));
        this.selectSkinBtn.active = false;
        this.list = new SkinListWidget(minecraft, this.width, this.height, TITLE_TEXT);
        this.children.add(this.list);
    }

    @Override
    public void tick(){
        super.tick();
        this.selectSkinBtn.active = this.list.getSelected() != null;
    }

    @Override
    public void closeScreen() {
        this.minecraft.displayGuiScreen(new LookCustomizationScreen(offhand));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.list.mouseScrolled(mouseX, mouseY, delta);
    }


    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderGlassBackground(0);
        this.list.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderGlassBackground(int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, new MatrixStack()));
    }
}
