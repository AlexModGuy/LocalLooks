package com.github.alexthe666.locallooks;


import com.github.alexthe666.locallooks.client.screen.LookCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = LocalLooks.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public void openMirrorGui(PlayerEntity playerEntity, boolean offhand){
        if(Minecraft.getInstance().player == playerEntity){
            Minecraft.getInstance().displayGuiScreen(new LookCustomizationScreen(offhand));
        }
    }

    public void displayItemInteractionForPlayer(PlayerEntity player, ItemStack stack) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
        }
    }
}
