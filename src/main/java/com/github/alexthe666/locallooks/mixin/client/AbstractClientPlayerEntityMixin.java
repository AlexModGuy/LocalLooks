package com.github.alexthe666.locallooks.mixin.client;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float f, GameProfile profile) {
        super(world, pos, f, profile);
    }

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getLocationSkin()Lnet/minecraft/util/ResourceLocation;",
            cancellable = true)
    private void locallooks_getLocationSkin(CallbackInfoReturnable<ResourceLocation> cir) {
        CompoundNBT tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if(tag.contains("LocalLooksSkin") && tag.getBoolean("LocalLooksSkin")){
            cir.setReturnValue(SkinLoader.getSkinForPlayer(this));
        }
    }

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getSkinType()Ljava/lang/String;",
            cancellable = true)
    private void locallooks_getSkinType(CallbackInfoReturnable<String> cir) {
        CompoundNBT tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if(tag.contains("LocalLooksArms") && tag.contains("LocalLooksArms")) {
            cir.setReturnValue(tag.getBoolean("LocalLooksArms") ? "slim" : "default");
        }
    }

}
