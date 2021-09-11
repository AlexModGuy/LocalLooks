package com.github.alexthe666.locallooks.client.screen;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.config.ConfigHolder;
import com.github.alexthe666.locallooks.message.CloseMirrorMessage;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LookCustomizationScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("locallooks:textures/gui/mirror.png");
    private static final ResourceLocation TEXTURE_SHEEN = new ResourceLocation("locallooks:textures/gui/mirror_sheen.png");
    private static final ITextComponent TITLE_TEXT = new TranslationTextComponent("gui.locallooks.mirror_title");
    private static final ITextComponent ENTER_URL_TEXT = new TranslationTextComponent("gui.locallooks.enter_url");
    private int sizePx = 250;
    private TextFieldWidget skinURLField;
    private String enteredURL;
    private float mousePosX;
    private float mousePosY;
    private int loadingWarning;
    private Button refreshURLBtn;
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
        this.addButton(refreshURLBtn = new Button(i + 128, j + 75, 80, 20, new TranslationTextComponent("gui.locallooks.refresh"), (p_214132_1_) -> {
            this.enteredURL = this.skinURLField.getText();
            loadingWarning = SkinLoader.testURL(enteredURL);
            this.changePlayerTexture(false, true, false);
        }));
        refreshURLBtn.active = false;
        this.skinURLField = new TextFieldWidget(this.font, i + 130, j + 50, 180, 20, new TranslationTextComponent("selectWorld.enterName")) {
            protected IFormattableTextComponent getNarrationMessage() {
                return super.getNarrationMessage().appendString(". ").appendSibling(ENTER_URL_TEXT).appendString(" ").appendString(LookCustomizationScreen.this.enteredURL);
            }
        };
        this.skinURLField.setMaxStringLength(50000);
        this.skinURLField.setText(this.enteredURL);
        this.skinURLField.setResponder((p_214319_1_) -> {
            this.enteredURL = p_214319_1_;
            this.refreshURLBtn.active = !this.skinURLField.getText().isEmpty();
        });
        this.children.add(this.skinURLField);
        this.addButton(new Button(i + 150, j + 160, 140, 20, new TranslationTextComponent("gui.locallooks.toggle_arms"), (p_214132_1_) -> {
            smallArms = !smallArms;
            this.changePlayerTexture(false, false, true);
        }));
        this.addButton(new Button(i + 150, j + 190, 140, 20, new TranslationTextComponent("gui.locallooks.reset"), (p_214132_1_) -> {
            this.changePlayerTexture(true, true, false);
        }));
        this.addButton(new Button(i + 150, j + 220, 140, 20, new TranslationTextComponent("gui.done"), (p_214132_1_) -> {
            Minecraft.getInstance().displayGuiScreen(null);
        }));
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.skinURLField.getText();
        this.init(minecraft, width, height);
        this.skinURLField.setText(s);
    }

    private void changePlayerTexture(boolean reset, boolean close, boolean armsOnly) {
        CompoundNBT tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
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
        if (smallArms != prevArms || !enteredURL.equals(prevURL) || prevNonVanillaSkin == vanillaSkin) {
            consumeMirror = true;
        }
        CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
        //"CitadelPatreonConfig" updates all citadel tags
        Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getEntityId()));
        transProgress = 5.0F;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (consumeMirror) {
            PlayerEntity player = Minecraft.getInstance().player;
            ItemStack stack = offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
            if (stack.getItem() == LocalLooks.MAGIC_MIRROR) {
                LocalLooks.PROXY.displayItemInteractionForPlayer(player, stack.copy());
                if (!isCreative() && ConfigHolder.SERVER.singleUseMirror.get()) {
                    stack.shrink(1);
                }
            }
            player.playSound(LocalLooks.MIRROR_SOUND, 1.0F, 1.0F);
            LocalLooks.sendMSGToServer(new CloseMirrorMessage(player.getEntityId(), offhand));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        this.mousePosX = (float) x;
        this.mousePosY = (float) y;
        int k = (this.width - this.sizePx - 200) / 2;
        int l = (this.height - this.sizePx + 10) / 2;
        drawString(matrixStack, this.font, TITLE_TEXT, k + 95, l + 4, 10526880);
        drawString(matrixStack, this.font, ENTER_URL_TEXT, k + 230, l + 30, 10526880);
        if (loadingWarning > 0) {
            drawString(matrixStack, this.font, new TranslationTextComponent("gui.locallooks.url_warning_" + loadingWarning), k + 320, l + 77, 0XFF0000);
        }
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        blit(matrixStack, k, l, 0.0F, 0.0F, this.sizePx, this.sizePx, this.sizePx, this.sizePx);
        InventoryScreen.drawEntityOnScreen(k + 125, l + 195, 70, (float) (k + 125) - this.mousePosX, (float) (l + 195 - 50) - this.mousePosY, Minecraft.getInstance().player);
        this.getMinecraft().getTextureManager().bindTexture(TEXTURE_SHEEN);
        sheenBlit(matrixStack.getLast().getMatrix(), k, l, this.sizePx, this.sizePx, 0.0F, 1.0F, 0.0F, 1.0F, partialTicks);
        matrixStack.push();
        this.skinURLField.render(matrixStack, x, y, partialTicks);
        matrixStack.pop();
        super.render(matrixStack, x, y, partialTicks);
    }


    private void sheenBlit(Matrix4f matrix, int x1, int y1, int w, int h, float minU, float maxU, float minV, float maxV, float partialTick) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        float offsetB = getBlitOffset() + 1000.0F;
        float startAlpha = 0.3F + MathHelper.sin((Minecraft.getInstance().player.ticksExisted + partialTick) * 0.01F) * 0.1F;
        float progress = 0.2F * MathHelper.lerp(partialTick, prevTransProgress, transProgress);
        float alpha = startAlpha + progress * (1F - startAlpha);
        bufferbuilder.pos(matrix, (float) x1, (float) y1 + h, offsetB).color(1, 1, 1, alpha).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float) x1 + w, (float) y1 + h, offsetB).color(1, 1, 1, alpha).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float) x1 + w, (float) y1, offsetB).color(1, 1, 1, alpha).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float) x1, (float) y1, offsetB).color(1, 1, 1, alpha).tex(minU, minV).endVertex();
        RenderSystem.enableBlend();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.disableBlend();
    }

}
