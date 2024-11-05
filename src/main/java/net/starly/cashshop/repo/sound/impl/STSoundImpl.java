package net.starly.cashshop.repo.sound.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.repo.sound.STSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STSoundImpl implements STSound {

    private static final CashShopMain plugin = CashShopMain.getPlugin();
    @Getter
    private final String templateName;
    private final SoundFunction function;

    public STSoundImpl(String templateName, List<String> sounds) {
        this.templateName = templateName;
        function = new SoundFunction(templateName, sounds.toArray(new String[0]), true);
    }

    @Override
    public void playSound(Player player) {
        function.play(player);
    }

    @Override
    public boolean isEmpty() {
        return function.isEmpty();
    }

    private static class SoundFunction {
        private SoundFunction next = null;
        private final List<SoundData> data = new ArrayList<>();
        private final boolean root;
        private long delay;

        private SoundFunction(String templateName, String[] array, boolean root) {
            this.root = root;
            for (String text : array) {
                String[] args = text.split(",");
                String type = args[0];
                try {
                    Sound sound = Sound.valueOf(type);
                    float pitch = Float.parseFloat(args[1]);
                    float volume = Float.parseFloat(args[2]);
                    long delay = 0;
                    try {
                        delay = Long.parseLong(args[3]);
                    } catch (Exception ignored) {
                    }
                    this.delay = delay;
                    data.add(new SoundData(sound, pitch, volume));
                    if (delay > 0) {
                        next = new SoundFunction(templateName, Arrays.copyOfRange(array, data.size(), array.length), false);
                        break;
                    }
                } catch (NumberFormatException ex0) {
                    plugin.getLogger().warning("[" + plugin.getName() + "] 사운드 " + templateName + " 의 설정 파일의 볼륨이나 피치값이 숫자가 아닙니다.");
                } catch (IndexOutOfBoundsException ex1) {
                    plugin.getLogger().warning("[" + plugin.getName() + "] 사운드 " + templateName + " 의 설정 파일의 설정이 올바르지않습니다. ('sound-type,pitch,volume[,delay]')");
                } catch (IllegalArgumentException e2) {
                    plugin.getLogger().warning("[" + plugin.getName() + "] 사운드 " + templateName + " 의 설정 파일의 " + type + " 타입은 찾을 수 없는 타입이거나 해당 버전에 맞지 않습니다.");
                }
            }
        }

        private boolean isEmpty() {
            return !root && (next == null || data.isEmpty());
        }

        private void play(Player player) {
            if (isEmpty()) return;
            data.forEach((it) -> it.play(player));
            if (next != null) {
                if (delay > 0) plugin.getServer().getScheduler().runTaskLater(plugin, () -> next.play(player), delay);
                else next.play(player);
            }
        }

        @Data
        @AllArgsConstructor
        private static class SoundData {
            private Sound sound;
            private float pitch;
            private float volume;

            private void play(Player player) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

}
