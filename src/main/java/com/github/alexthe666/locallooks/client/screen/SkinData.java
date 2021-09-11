package com.github.alexthe666.locallooks.client.screen;

import com.github.alexthe666.locallooks.skin.SkinType;
import net.minecraft.util.ResourceLocation;

public class SkinData {
    private boolean vanilla;
    private String url;
    private ResourceLocation key;
    private String name;
    private SkinType type;

    public SkinData(boolean vanilla) {
        this.vanilla = vanilla;
    }

    public boolean isVanilla() {
        return vanilla;
    }

    public void setVanilla(boolean vanilla) {
        this.vanilla = vanilla;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkinType getType() {
        return type;
    }

    public void setType(SkinType type) {
        this.type = type;
    }
}
