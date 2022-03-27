package com.github.alexthe666.locallooks.client.screen;


import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.client.gui.screen.WorldSelectionList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class SkinListWidget extends ExtendedList<SkinListWidget.Entry> {
    private static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("locallooks:textures/gui/missing_skin.png");
    private final ITextComponent name;

    public SkinListWidget(Minecraft mc, int width, int height, ITextComponent name) {
        super(mc, width, height, 30, height - 55 + 4, 70);
        this.name = name;
        this.centerListVertically = false;
        this.setRenderHeader(true, (int)(9.0F * 1.5F));
        this.func_244605_b(false);
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
            List<File> files = (List<File>) FileUtils.listFiles(parent, extensions, true);
            for (File file : files) {
                this.addEntry(new Entry(file));
            }
        }catch (Exception e){
            LocalLooks.LOGGER.warn("could not open skin folder");
        }
    }

    protected void renderHeader(MatrixStack matrixStack, int x, int y, Tessellator tessellator) {
    }

    public int getRowWidth() {
        return this.width;
    }

    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ResourceLocation prev = AbstractGui.BACKGROUND_LOCATION;
        AbstractGui.BACKGROUND_LOCATION = LocalSkinSelectionScreen.BACKGROUND_LOCATION;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        AbstractGui.BACKGROUND_LOCATION = prev;
    }

    public final class Entry extends ExtendedList.AbstractListEntry<Entry>{
        private final File file;
        private ResourceLocation resourceLocation;
        @Nullable
        private final Texture texture;
        private StringTextComponent fileName;
        private boolean selectable = false;

        public Entry(File file) {
            this.file = file;
            this.resourceLocation = new ResourceLocation("locallooks", "localskins/" + file.getName());
            this.texture = getTextureForRender();
            fileName = new StringTextComponent(file.getName());
        }

        @Override
        public void render(MatrixStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.push();
            Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
            RenderSystem.enableBlend();
            AbstractGui.blit(matrixStack, left, top, 0.0F, 0.0F, 64, 64, 64, 64);
            RenderSystem.disableBlend();
            matrixStack.pop();
            matrixStack.push();
            Minecraft.getInstance().fontRenderer.drawText(matrixStack, fileName, (float)(left + 64), (float)(top + 6), selectable ? 16777215 : 0XFF0000);
            if(!selectable){
                Minecraft.getInstance().fontRenderer.drawText(matrixStack, new TranslationTextComponent("gui.locallooks.url_warning_4"), (float)(left + 64), (float)(top + 26), 0XAA0000);
            }
            matrixStack.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && selectable) {
                SkinListWidget.this.setSelected(this);
            }

            return false;
        }


        @Nullable
        private Texture getTextureForRender() {
            boolean flag = file != null && file.isFile();
            if (flag && resourceLocation != MISSING_TEXTURE) {
                try (InputStream inputstream = new FileInputStream(file)) {
                    NativeImage nativeimage = NativeImage.read(inputstream);
                    if(nativeimage.getWidth() == nativeimage.getHeight()){
                        DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
                        Minecraft.getInstance().getTextureManager().loadTexture(resourceLocation, dynamictexture);
                        this.selectable = true;
                        return dynamictexture;
                    }
                } catch (Throwable throwable) {
                    LocalLooks.LOGGER.error("Invalid icon for skin {}", file.getName(), throwable);
                    resourceLocation = MISSING_TEXTURE;
                }
            } else {
                Minecraft.getInstance().getTextureManager().deleteTexture(resourceLocation);
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
    }
}
