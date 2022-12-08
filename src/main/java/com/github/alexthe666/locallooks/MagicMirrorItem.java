package com.github.alexthe666.locallooks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

public class MagicMirrorItem extends Item {

    public MagicMirrorItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }


    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        LocalLooks.PROXY.openMirrorGui(playerIn, handIn == InteractionHand.OFF_HAND);
        return InteractionResultHolder.success(stack);
    }
}
