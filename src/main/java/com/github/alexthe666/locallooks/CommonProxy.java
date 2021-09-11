package com.github.alexthe666.locallooks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LocalLooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public void clientInit() {
    }

    public void init() {
    }

    public void displayItemInteractionForPlayer(PlayerEntity player, ItemStack stack){
    }

    public void openMirrorGui(PlayerEntity playerEntity, boolean offhand){

    }

    public void setupParticles() {

    }
}
