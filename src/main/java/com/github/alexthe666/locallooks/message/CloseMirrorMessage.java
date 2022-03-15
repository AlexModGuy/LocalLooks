package com.github.alexthe666.locallooks.message;

import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.config.ConfigHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CloseMirrorMessage  {
    private int entityID;
    private boolean offhand;

    public CloseMirrorMessage(){

    }

    public CloseMirrorMessage(int entityID, boolean offhand) {
        this.entityID = entityID;
        this.offhand = offhand;
    }

    public static void write(CloseMirrorMessage message, FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(message.entityID);
        packetBuffer.writeBoolean(message.offhand);
    }

    public static CloseMirrorMessage read(FriendlyByteBuf packetBuffer) {
        return new CloseMirrorMessage(packetBuffer.readInt(), packetBuffer.readBoolean());
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(CloseMirrorMessage message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
                Entity e = ((NetworkEvent.Context)context.get()).getSender().level.getEntity(message.entityID);
                if(e instanceof Player){
                    ItemStack stack = message.offhand ? ((Player) e).getOffhandItem() : ((Player) e).getMainHandItem();
                    if(stack.getItem() == LocalLooks.MAGIC_MIRROR.get()){
                        LocalLooks.PROXY.displayItemInteractionForPlayer((Player) e, stack.copy());
                        if(!((Player) e).isCreative() && ConfigHolder.SERVER.singleUseMirror.get()){
                            stack.shrink(1);
                        }
                    }
                }

            });
        }
    }
}
