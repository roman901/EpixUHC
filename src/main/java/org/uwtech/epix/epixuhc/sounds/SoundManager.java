package org.uwtech.epix.epixuhc.sounds;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {
    private Sound getSound(UHCSound uhcSound){
        return Sound.valueOf(getStringSound(uhcSound));
    }

    private String getStringSound(UHCSound uhcSound){
        return uhcSound.getSound();
    }

    public void playSoundTo(Player player, UHCSound uhcSound, float v, float v1){
        player.playSound(player.getLocation(), getSound(uhcSound),v,v1);
    }

}
