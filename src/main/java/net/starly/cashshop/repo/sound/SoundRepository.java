package net.starly.cashshop.repo.sound;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.repo.sound.impl.STSoundImpl;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SoundRepository {

    private static SoundRepository instance;

    public static SoundRepository getInstance() {
        if (instance == null) instance = new SoundRepository();
        return instance;
    }

    private SoundRepository() {
    }

    private final Map<String, STSound> soundMap = new HashMap<>();
    private List<String> soundKeyList;

    public STSound getSound(String key) {
        if (key == null || key.isEmpty()) return null;
        if (soundMap.containsKey(key))
            return soundMap.get(key);
        return null;
    }

    private List<String> getSounds() {
        return new ArrayList<>(soundMap.keySet());
    }

    public String next(String soundPair) {
        if (soundKeyList.isEmpty()) return null;
        int index;
        if (soundPair == null) index = 0;
        else index = soundKeyList.indexOf(soundPair) + 1;

        if (index >= soundKeyList.size()) return null;
        return soundKeyList.get(index);
    }

    public void initializing(CashShopMain plugin) {
        if (!soundMap.isEmpty()) soundMap.clear();
        File dataFolder = plugin.getDataFolder();
        File soundFolder = new File(dataFolder, "sounds");
        if (!soundFolder.exists()) {
            if (soundFolder.mkdirs()) {
                createTemplate(1, dataFolder, soundFolder, plugin);
                createTemplate(2, dataFolder, soundFolder, plugin);
                createTemplate(3, dataFolder, soundFolder, plugin);
                createTemplate(4, dataFolder, soundFolder, plugin);
                createTemplate(5, dataFolder, soundFolder, plugin);
            }
        }

        File[] files = soundFolder.listFiles();
        if (files == null) return;
        Arrays.stream(files).filter((it) -> it.getName().endsWith(".yml")).collect(Collectors.toList()).forEach((file) -> {
            try {
                String key = file.getName();
                key = key.substring(0, key.length() - 4);
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String templateName = ChatColor.translateAlternateColorCodes('&', config.getString("name"));
                soundMap.put(key, new STSoundImpl(templateName, config.getStringList("sound-function")));
            } catch (Exception ignored) {
                plugin.getLogger().warning("[" + plugin.getName() + "]" + file.getName() + " 파일의 설정이 올바르지 않습니다.");
            }
        });
        soundKeyList = getSounds().stream().sorted().collect(Collectors.toList());
    }

    private void createTemplate(int index, File dataFolder, File soundFolder, JavaPlugin plugin) {
        String prefix;
        if (plugin.getServer().getVersion().contains("1.12")) prefix = "low_version_sounds/";
        else prefix = "high_version_sounds/";
        plugin.saveResource(prefix + "sound-template" + index + ".yml", true);
        File source = new File(dataFolder, prefix + "sound-template" + index + ".yml");
        if (source.exists()) {
            File destination = new File(soundFolder, "sound-template" + index + ".yml");
            source.renameTo(destination);
            source.delete();
        }
    }

}
