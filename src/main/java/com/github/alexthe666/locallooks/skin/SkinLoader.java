package com.github.alexthe666.locallooks.skin;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.locallooks.LocalLooks;
import com.github.alexthe666.locallooks.skin.texture.MirrorDownloadingTexture;
import com.google.common.hash.Hashing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkinLoader {
    private static final String BACKUP_URL = "https://i.imgur.com/38cvzEc.png";

    private static File getSkinCacheFolder() {
        Path configPath = FMLPaths.GAMEDIR.get();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "locallooks/cache");
        return jsonPath.toFile();
    }

    public static File getSkinFolder() {
        Path configPath = FMLPaths.GAMEDIR.get();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), "locallooks/skins");
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectory(jsonPath);
                LocalLooks.LOGGER.info("Created skin folder for locallooks");
            }catch (Exception e){
                return null;
            }
        }
        return jsonPath.toFile();
    }

    private static ResourceLocation loadSkin(String urlStr, boolean thinArms) {
        String s = Hashing.sha1().hashUnencodedChars(urlStr).toString();


        ResourceLocation resourcelocation = new ResourceLocation("locallooks:skins/" + s + ".png");
        Texture texture = Minecraft.getInstance().textureManager.getTexture(resourcelocation);
        if (texture == null) {
            File file1 = new File(getSkinCacheFolder(), s.length() > 2 ? s.substring(0, 2) : "xx");
            File file2 = new File(file1, s);
            URL url = null;
            try {
                url = new URL(urlStr);
                URLConnection urlConn = url.openConnection();
                urlConn.addRequestProperty("User-Agent", "Mozilla/4.76");
                urlStr = urlConn.getURL().toString();
            } catch (Exception e) {

            }
            MirrorDownloadingTexture downloadingtexture = new MirrorDownloadingTexture(file2, urlStr, DefaultPlayerSkin.getDefaultSkinLegacy(), !thinArms, () -> {
            });
            Minecraft.getInstance().textureManager.loadTexture(resourcelocation, downloadingtexture);
        }

        return resourcelocation;
    }

    public static ResourceLocation getSkinForPlayer(PlayerEntity player) {
        String url = BACKUP_URL;
        boolean arms = false;
        CompoundNBT tag = CitadelEntityData.getOrCreateCitadelTag(player);
        if (tag.contains("LocalLooksURL")) {
            url = tag.getString("LocalLooksURL");
        }
        if (tag.contains("LocalLooksArms")) {
            arms = tag.getBoolean("LocalLooksArms");
        }
        return loadSkin(url, arms);
    }

    public static int testURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();
            urlConn.addRequestProperty("User-Agent", "Mozilla/4.76");
            String contentType = urlConn.getContentType();
            if (contentType != null && contentType.contains("image")) {
                return 0;
            }
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                return 0;
            } else {
                return 1;
            }
        } catch (MalformedURLException e) {
            return 2;
        } catch (IOException e) {
            // e.printStackTrace();
            return 3;
        }
    }
}
