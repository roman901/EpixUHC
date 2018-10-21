package org.uwtech.epix.epixuhc.sounds;

public enum UHCSound {
    CLICK("UI_BUTTON_CLICK"),
    ENDER_DRAGON_GROWL("ENTITY_ENDER_DRAGON_GROWL"),
    WITHER_SPAWN("ENTITY_WITHER_SPAWN"),
    FIREWORK_LAUNCH("ENTITY_FIREWORK_LAUNCH");

    private String sound;
    UHCSound(String sound) {
        this.sound = sound;
    }

    public String getSound() {
        return this.sound;
    }
}