package com.github.alexthe666.locallooks;

import com.github.alexthe666.locallooks.config.ConfigHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = LocalLooks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LocalLooksEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfigHolder.SERVER.giveMirrorOnStartup.get()) {
            CompoundTag playerData = event.getEntity().getPersistentData();
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("locallooks_has_mirror")) {
                ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(LocalLooks.MAGIC_MIRROR.get()));
                data.putBoolean("locallooks_has_mirror", true);
                playerData.put(Player.PERSISTED_NBT_TAG, data);
            }
        }
    }
}
