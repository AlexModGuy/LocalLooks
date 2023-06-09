package com.github.alexthe666.locallooks.client.screen;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.config.ConfigHolder;
import com.github.alexthe666.locallooks.message.CloseMirrorMessage;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;

import java.io.File;
import java.net.MalformedURLException;

public class LookCustomizationScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("locallooks:textures/gui/mirror.png");
    private static final ResourceLocation TEXTURE_SHEEN = new ResourceLocation("locallooks:textures/gui/mirror_sheen.png");
    private static final Component TITLE_TEXT = Component.translatable("gui.locallooks.mirror_title");
    private static final Component ENTER_URL_TEXT = Component.translatable("gui.locallooks.enter_url");
    private int sizePx = 250;
    private EditBox skinURLField;
    private String enteredURL;
    private float mousePosX;
    private float mousePosY;
    private int loadingWarning;
    private Button refreshURLBtn;
    private Button selectFileBtn;
    private boolean smallArms = false;
    private float transProgress = 0;
    private float prevTransProgress = 0;
    private boolean offhand;
    private boolean consumeMirror;

    public LookCustomizationScreen(boolean offhand) {
        super(TITLE_TEXT);
        this.enteredURL = "";
        this.loadingWarning = 0;
        transProgress = 0.0F;
        this.offhand = offhand;
    }


    public LookCustomizationScreen(boolean offhand, File fileIn) {
        super(TITLE_TEXT);
        this.loadingWarning = 0;
        transProgress = 0.0F;
        this.offhand = offhand;
        try {
            this.enteredURL = fileIn.toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.changePlayerTexture(false, false, false, true);
    }


    private boolean isCreative() {
        return Minecraft.getInstance().player.isCreative() || Minecraft.getInstance().player.isSpectator();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevTransProgress = transProgress;
        this.skinURLField.tick();
        if (transProgress > 0.0F) {
            transProgress -= 0.5F;
        }
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.sizePx) / 2;
        int j = (this.height - this.sizePx) / 2;
        this.addRenderableWidget(refreshURLBtn = Button.builder(Component.translatable("gui.locallooks.refresh"), (button) -> {
            this.enteredURL = this.skinURLField.getValue();
            loadingWarning = SkinLoader.testURL(enteredURL);
            this.changePlayerTexture(false, true, false,  false);
        }).size(100, 20).pos(i + 128, j + 75).build());

        refreshURLBtn.active = false;
        this.skinURLField = new EditBox(this.font, i + 130, j + 50, 180, 20, Component.translatable("selectWorld.enterName")) {
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(ENTER_URL_TEXT).append(" ").append(LookCustomizationScreen.this.enteredURL);
            }
        };
        this.skinURLField.setMaxLength(50000);
        this.skinURLField.setValue(this.enteredURL);
        this.skinURLField.setResponder((p_214319_1_) -> {
            this.enteredURL = p_214319_1_;
            this.refreshURLBtn.active = !this.skinURLField.getValue().isEmpty();
        });
        this.addRenderableWidget(this.skinURLField);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.locallooks.toggle_arms"), (button) -> {
            smallArms = !smallArms;
            this.changePlayerTexture(false, false, true, false);
        }).size(140, 20).pos(i + 150, j + 160).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.locallooks.reset"), (button) -> {
            this.changePlayerTexture(true, true, false,  false);
        }).size(140, 20).pos(i + 150, j + 190).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), (button) -> {
            onClose();
            Minecraft.getInstance().setScreen(null);
        }).size(140, 20).pos(i + 150, j + 220).build());

        this.addRenderableWidget(selectFileBtn = Button.builder(Component.translatable("gui.locallooks.select_file"), (button) -> {
            Minecraft.getInstance().setScreen(new LocalSkinSelectionScreen(offhand));
        }).size(100, 20).pos(i + 128, j + 100).build());
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.skinURLField.getValue();
        this.init(minecraft, width, height);
        this.skinURLField.setValue(s);
    }

    private void changePlayerTexture(boolean reset, boolean close, boolean armsOnly, boolean localFile) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        boolean prevArms = false;
        String prevURL = "";
        boolean prevNonVanillaSkin = false;
        boolean vanillaSkin = false;
        if(tag.contains("LocalLooksArms")){
            prevArms = tag.getBoolean("LocalLooksArms");
        }
        if(tag.contains("LocalLooksURL")){
            prevURL = tag.getString("LocalLooksURL");
        }
        if(tag.contains("LocalLooksSkin")){
            prevNonVanillaSkin = tag.getBoolean("LocalLooksSkin");
        }
        if (reset) {
            vanillaSkin = true;
            tag.putBoolean("LocalLooksSkin", false);
            tag.putString("LocalLooksURL", "");
        } else if (armsOnly) {
            tag.putBoolean("LocalLooksArms", smallArms);
        } else {
            vanillaSkin = false;
            tag.putBoolean("LocalLooksSkin", true);
            tag.putBoolean("LocalLooksArms", smallArms);
            tag.putString("LocalLooksURL", enteredURL);
        }
        if(localFile){
            tag.putBoolean("LocalLooksIsLocalSkin", localFile);
        }
        if (smallArms != prevArms || !enteredURL.equals(prevURL) || prevNonVanillaSkin == vanillaSkin) {
            consumeMirror = true;
        }
        CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
        //"CitadelPatreonConfig" updates all citadel tags
        Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
        transProgress = 5.0F;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (consumeMirror) {
            Player player = Minecraft.getInstance().player;
            ItemStack stack = offhand ? player.getOffhandItem() : player.getMainHandItem();
            if (stack.getItem() == LocalLooks.MAGIC_MIRROR.get()) {
                LocalLooks.PROXY.displayItemInteractionForPlayer(player, stack.copy());
                if (!isCreative() && ConfigHolder.SERVER.singleUseMirror.get()) {
                    stack.shrink(1);
                }
            }
            player.playSound(LocalLooks.MIRROR_SOUND.get(), 1.0F, 1.0F);
            LocalLooks.sendMSGToServer(new CloseMirrorMessage(player.getId(), offhand));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, x, y, partialTicks);

        float f = (float)Math.atan((double)(-x / 40.0F));
        float f1 = (float)Math.atan((double)(-y / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(-(float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);


        this.mousePosX = (float) x;
        this.mousePosY = (float) y;
        int k = (this.width - this.sizePx - 200) / 2;
        int l = (this.height - this.sizePx + 10) / 2;
        guiGraphics.drawString(this.font, TITLE_TEXT, k + 95, l + 4, 10526880);
        guiGraphics.drawString(this.font, ENTER_URL_TEXT, k + 230, l + 30, 10526880);
        if (loadingWarning > 0) {
            guiGraphics.drawString(this.font, Component.translatable("gui.locallooks.url_warning_" + loadingWarning), k + 320, l + 77, 0XFF0000);
        }
        guiGraphics.blit(TEXTURE, k, l, 0.0F, 0.0F, this.sizePx, this.sizePx, this.sizePx, this.sizePx);

        Player entity = Minecraft.getInstance().player;
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        InventoryScreen.renderEntityInInventory(guiGraphics, k + 125, l + 195, 70, quaternionf, quaternionf1, entity);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE_SHEEN);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        sheenBlit(guiGraphics.pose().last().pose(), k, l, this.sizePx, this.sizePx, 0.0F, 1.0F, 0.0F, 1.0F, partialTicks);
        guiGraphics.pose().pushPose();
        this.skinURLField.render(guiGraphics, x, y, partialTicks);
        guiGraphics.pose().popPose();
    }


    private void sheenBlit(Matrix4f matrix, int x1, int y1, int w, int h, float minU, float maxU, float minV, float maxV, float partialTick) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        float offsetB =  1000.0F;
        float startAlpha = 0.3F + Mth.sin((Minecraft.getInstance().player.tickCount + partialTick) * 0.01F) * 0.1F;
        float progress = 0.2F * Mth.lerp(partialTick, prevTransProgress, transProgress);
        float alpha = startAlpha + progress * (1F - startAlpha);
        bufferbuilder.vertex(matrix, (float) x1, (float) y1 + h, offsetB).color(1, 1, 1, alpha).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float) x1 + w, (float) y1 + h, offsetB).color(1, 1, 1, alpha).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float) x1 + w, (float) y1, offsetB).color(1, 1, 1, alpha).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, (float) x1, (float) y1, offsetB).color(1, 1, 1, alpha).uv(minU, minV).endVertex();
        RenderSystem.enableBlend();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }



}
