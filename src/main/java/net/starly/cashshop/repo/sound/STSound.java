package net.starly.cashshop.repo.sound;

import org.bukkit.entity.Player;

public interface STSound {

    void playSound(Player player);

    boolean isEmpty();

    String getTemplateName();

}
