package com.github.alexthe666.locallooks.client.screen;


import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import net.minecraft.network.chat.Component;

public class SkinListWidget extends ObjectSelectionList<SkinListWidget.Entry> {
    private static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("locallooks:textures/gui/missing_skin.png");
    private final Component name;

    public SkinListWidget(Minecraft mc, int width, int height, Component name) {
        super(mc, width, height, 30, height - 55 + 4, 70);
        this.name = name;
        this.centerListVertically = false;
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
        this.setRenderBackground(false);
        this.repopulate();
    }

    public void repopulate() {
        this.clearEntries();
        File parent = SkinLoader.getSkinFolder();
        if(parent != null && !parent.exists()){
            parent.mkdir();
        }
        String[] extensions = new String[] { "png", "jpg" };
        try{
            if(parent != null){
                List<File> files = (List<File>) FileUtils.listFiles(parent, extensions, true);
                for (File file : files) {
                    this.addEntry(new Entry(file));
                }
            }
        }catch (Exception e){
            LocalLooks.LOGGER.warn("could not open skin folder");
        }
    }

    protected void renderHeader(PoseStack matrixStack, int x, int y, Tesselator tessellator) {
    }

    public int getRowWidth() {
        return this.width;
    }

    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ResourceLocation prev = Screen.BACKGROUND_LOCATION;
        Screen.BACKGROUND_LOCATION = LocalSkinSelectionScreen.BACKGROUND_LOCATION;
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        Screen.BACKGROUND_LOCATION = prev;
    }

    public final class Entry extends ObjectSelectionList.Entry<Entry>{
        private final File file;
        private ResourceLocation resourceLocation;
        @Nullable
        private final AbstractTexture texture;
        private String fileName;
        private boolean selectable = false;

        public Entry(File file) {
            this.file = file;
            this.resourceLocation = new ResourceLocation("locallooks", "localskins/" + file.getName());
            this.texture = getTextureForRender();
            fileName = file.getName();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            guiGraphics.pose().pushPose();
            RenderSystem.enableBlend();
            RenderSystem.enableBlend();
            guiGraphics.blit(resourceLocation, left, top, 0.0F, 0.0F, 64, 64, 64, 64);
            RenderSystem.disableBlend();
            guiGraphics.pose().popPose();
            guiGraphics.pose().pushPose();
            guiGraphics.drawString(Minecraft.getInstance().font, fileName, (float)(left + 64), (float)(top + 6), selectable ? 16777215 : 0XFF0000, false);
            if(!selectable){
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("gui.locallooks.url_warning_4"), (left + 64), (top + 26), 0XAA0000, true);
            }
            guiGraphics.pose().popPose();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && selectable) {
                SkinListWidget.this.setSelected(this);
            }

            return false;
        }


        @Nullable
        private AbstractTexture getTextureForRender() {
            boolean flag = file != null && file.isFile();
            if (flag && resourceLocation != MISSING_TEXTURE) {
                try (InputStream inputstream = new FileInputStream(file)) {
                    NativeImage nativeimage = NativeImage.read(inputstream);
                    if(nativeimage.getWidth() == nativeimage.getHeight()){
                        DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
                        Minecraft.getInstance().getTextureManager().register(resourceLocation, dynamictexture);
                        this.selectable = true;
                        return dynamictexture;
                    }
                } catch (Throwable throwable) {
                    LocalLooks.LOGGER.error("Invalid icon for skin {}", file.getName(), throwable);
                    resourceLocation = MISSING_TEXTURE;
                }
            } else {
                Minecraft.getInstance().getTextureManager().release(resourceLocation);
            }
            resourceLocation = MISSING_TEXTURE;
            return Minecraft.getInstance().getTextureManager().getTexture(MISSING_TEXTURE);
        }

        public void close() {
            if (this.texture != null) {
                this.texture.close();
            }

        }

        public File getFile() {
            return file;
        }

        @Override
        public Component getNarration() {
            return name;
        }
    }
}
