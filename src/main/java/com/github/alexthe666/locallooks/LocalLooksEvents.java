package com.github.alexthe666.locallooks;

import com.github.alexthe666.locallooks.config.ConfigHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = LocalLooks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LocalLooksEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigHolder.SERVER.giveMirrorOnStartup.get()) {
            CompoundNBT playerData = event.getPlayer().getPersistentData();
            CompoundNBT data = playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("locallooks_has_mirror")) {
                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(LocalLooks.MAGIC_MIRROR));
                data.putBoolean("locallooks_has_mirror", true);
                playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
            }
        }
    }
}
