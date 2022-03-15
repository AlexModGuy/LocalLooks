package com.github.alexthe666.locallooks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LocalLooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public void clientInit() {
    }

    public void init() {
    }

    public void displayItemInteractionForPlayer(Player player, ItemStack stack){
    }

    public void openMirrorGui(Player playerEntity, boolean offhand){

    }

    public void setupParticles() {

    }
}
