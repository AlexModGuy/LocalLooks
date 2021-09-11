package com.github.alexthe666.locallooks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MagicMirrorItem extends Item {

    public MagicMirrorItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).maxStackSize(1).group(ItemGroup.MISC));
        this.setRegistryName("locallooks:magic_mirror");
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }


    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        LocalLooks.PROXY.openMirrorGui(playerIn, handIn == Hand.OFF_HAND);
        return ActionResult.resultSuccess(stack);
    }
}
