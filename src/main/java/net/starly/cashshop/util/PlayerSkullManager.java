package net.starly.cashshop.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.exception.UnSupportedVersionException;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkullManager {

    private static final Map<UUID, String> skinTagMap = new HashMap<>();
    private static boolean highVersion;
    static {
        highVersion = VersionController.getInstance().getVersion().isHighVersion();
    }

    private static String getSkinTag(UUID uniqueId) {
        if(skinTagMap.containsKey(uniqueId))
            return skinTagMap.get(uniqueId);
        return null;
    }

    public static ItemStack getCustomSkull(String tempTag) throws UnSupportedVersionException {
        ItemStack baseItem;
        try {
            if(highVersion) baseItem = new ItemStack(Material.valueOf("PLAYER_HEAD"));
            else baseItem = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        } catch (Exception ignore) {
            return new ItemStack(Material.STONE);
        }

        String url = "https://textures.minecraft.net/texture/" + tempTag;
        ItemMeta headMeta = baseItem.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] byteArray = String.format("{texture:{SKIN:{url:\"%s\"}}}", url).getBytes();
        byte[] encodedData = launchBase64Method(byteArray, highVersion);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
            baseItem.setItemMeta(headMeta);
            return baseItem ;
        }
        catch (Exception e) { throw new UnSupportedVersionException(CashShopMain.getPlugin().getServer().getVersion()); }
    }

    public static ItemStack getPlayerSkull(UUID targetUniqueId) {
        String skinTag;
        if(skinTagMap.containsKey(targetUniqueId)) skinTag = skinTagMap.get(targetUniqueId);
        else {
            try {
                String contents = getURLContents("https://sessionserver.mojang.com/session/minecraft/profile/" + targetUniqueId);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(contents, JsonObject.class);
                String value = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                String decoded = new String(java.util.Base64.getDecoder().decode(value));
                JsonObject newObject = gson.fromJson(decoded, JsonObject.class);
                String skinUrl = newObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                byte[] skin = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}").getBytes();
                byte[] encoded = java.util.Base64.getEncoder().encode(skin);
                long hash = Arrays.hashCode(encoded);
                UUID hashAsId = new UUID(hash, hash);
                skinTag = "{SkullOwner:{Id:\"" + hashAsId + "\", Properties:{textures:[{Value:\"" + value + "\"}]}}}";
                skinTagMap.put(targetUniqueId, skinTag);
            } catch (Exception e) { return new ItemStack(Material.STONE); }
        }
        ItemStack baseItem;

        try {
            if(highVersion) baseItem = new ItemStack(Material.valueOf("PLAYER_HEAD"));
            else baseItem = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        } catch (Exception ignore) {
            return new ItemStack(Material.STONE);
        }
        return CashShopMain.getPlugin().getServer().getUnsafe().modifyItemStack(baseItem, skinTag);
    }

    private static String getURLContents(String stringUrl) throws UnSupportedVersionException {
        try {
            URL url = new URL(stringUrl);
            StringBuilder builder = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                br.lines().forEach(builder::append);
                return builder.toString();
            } catch (Exception e) { throw new UnSupportedVersionException(CashShopMain.getPlugin().getServer().getVersion()); }
        } catch (Exception e) { throw new UnSupportedVersionException(CashShopMain.getPlugin().getServer().getVersion()); }
    }

    private static byte[] launchBase64Method(byte[] byteArray, boolean highVersion) throws UnSupportedVersionException{
        if(!highVersion) return Base64.encodeBase64(byteArray);
        else {
            try {
                Method method = Class.forName("org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64").getMethod("encodeBase64", byte[].class);
                return (byte[]) method.invoke(null, byteArray);
            } catch (Exception ignored) {
                throw new UnSupportedVersionException(CashShopMain.getPlugin().getServer().getVersion());
            }
        }
    }

}
