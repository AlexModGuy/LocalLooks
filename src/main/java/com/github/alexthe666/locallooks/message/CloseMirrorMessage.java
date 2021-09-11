package com.github.alexthe666.locallooks.message;

import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.config.ConfigHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void write(CloseMirrorMessage message, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(message.entityID);
        packetBuffer.writeBoolean(message.offhand);
    }

    public static CloseMirrorMessage read(PacketBuffer packetBuffer) {
        return new CloseMirrorMessage(packetBuffer.readInt(), packetBuffer.readBoolean());
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(CloseMirrorMessage message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
                Entity e = ((NetworkEvent.Context)context.get()).getSender().world.getEntityByID(message.entityID);
                if(e instanceof PlayerEntity){
                    ItemStack stack = message.offhand ? ((PlayerEntity) e).getHeldItemOffhand() : ((PlayerEntity) e).getHeldItemMainhand();
                    if(stack.getItem() == LocalLooks.MAGIC_MIRROR){
                        LocalLooks.PROXY.displayItemInteractionForPlayer((PlayerEntity) e, stack.copy());
                        if(!((PlayerEntity) e).isCreative() && ConfigHolder.SERVER.singleUseMirror.get()){
                            stack.shrink(1);
                        }
                    }
                }

            });
        }
    }
}
