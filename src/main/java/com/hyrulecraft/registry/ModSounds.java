package com.hyrulecraft.registry;

import com.hyrulecraft.HyruleCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    private ModSounds() {}

    /** Orden de OcarinaNote: A, ABAJO, DERECHA, IZQUIERDA, ARRIBA */
    public static final SoundEvent[] NOTES = new SoundEvent[5];
    public static final SoundEvent SONG_OK = reg("song_ok");
    public static final SoundEvent Z_TARGET = reg("z_target");
    public static final SoundEvent NAVI = reg("navi_ping");
    public static final SoundEvent ROLL = reg("roll");
    public static final SoundEvent RUPEE = reg("rupee");
    public static final SoundEvent SECRET = reg("secret");
    public static final SoundEvent HOOKSHOT = reg("hookshot");
    public static final SoundEvent SPIN = reg("spin_charge");

    static {
        String[] names = {"a", "down", "right", "left", "up"};
        for (int i = 0; i < 5; i++) NOTES[i] = reg("ocarina_" + names[i]);
    }

    private static SoundEvent reg(String id) {
        Identifier ident = new Identifier(HyruleCraft.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, ident, SoundEvent.of(ident));
    }

    public static void init() {}
}
