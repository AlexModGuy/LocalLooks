package com.github.alexthe666.locallooks.mixin.client;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.locallooks.skin.SkinLoader;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player {


    public AbstractClientPlayerEntityMixin(Level level, BlockPos pos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey key) {
        super(level, pos, f, gameProfile, key);
    }

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/client/player/AbstractClientPlayer;getSkinTextureLocation()Lnet/minecraft/resources/ResourceLocation;",
            cancellable = true)
    private void locallooks_getLocationSkin(CallbackInfoReturnable<ResourceLocation> cir) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if(tag.contains("LocalLooksSkin") && tag.getBoolean("LocalLooksSkin")){
            cir.setReturnValue(SkinLoader.getSkinForPlayer(this));
        }
    }

    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/client/player/AbstractClientPlayer;getModelName()Ljava/lang/String;",
            cancellable = true)
    private void locallooks_getSkinType(CallbackInfoReturnable<String> cir) {
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        if (tag.contains("LocalLooksArms") && tag.contains("LocalLooksArms")) {
            cir.setReturnValue(tag.getBoolean("LocalLooksArms") ? "slim" : "default");
        }
    }
}
