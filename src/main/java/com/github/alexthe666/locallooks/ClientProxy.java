package com.github.alexthe666.locallooks;


import com.github.alexthe666.locallooks.client.screen.LookCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = LocalLooks.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public void openMirrorGui(Player playerEntity, boolean offhand){
        if(Minecraft.getInstance().player == playerEntity){
            Minecraft.getInstance().setScreen(new LookCustomizationScreen(offhand));
        }
    }

    public void displayItemInteractionForPlayer(Player player, ItemStack stack) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
        }
    }
}
