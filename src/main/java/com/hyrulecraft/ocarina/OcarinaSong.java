package com.hyrulecraft.ocarina;

import java.util.List;

import static com.hyrulecraft.ocarina.OcarinaNote.*;

/** Las 12 canciones de Ocarina of Time (secuencias de botones originales). */
public enum OcarinaSong {
    ZELDAS_LULLABY("zeldas_lullaby", null, LEFT, UP, RIGHT, LEFT, UP, RIGHT),
    EPONAS_SONG("eponas_song", null, UP, LEFT, RIGHT, UP, LEFT, RIGHT),
    SARIAS_SONG("sarias_song", null, DOWN, RIGHT, LEFT, DOWN, RIGHT, LEFT),
    SUNS_SONG("suns_song", null, RIGHT, DOWN, UP, RIGHT, DOWN, UP),
    SONG_OF_TIME("song_of_time", null, RIGHT, A, DOWN, RIGHT, A, DOWN),
    SONG_OF_STORMS("song_of_storms", null, A, DOWN, UP, A, DOWN, UP),
    MINUET_OF_FOREST("minuet_of_forest", "forest", A, UP, LEFT, RIGHT, LEFT, RIGHT),
    BOLERO_OF_FIRE("bolero_of_fire", "fire", DOWN, A, DOWN, A, RIGHT, DOWN, RIGHT, DOWN),
    SERENADE_OF_WATER("serenade_of_water", "water", A, DOWN, RIGHT, RIGHT, LEFT),
    REQUIEM_OF_SPIRIT("requiem_of_spirit", "spirit", A, DOWN, A, RIGHT, DOWN, A),
    NOCTURNE_OF_SHADOW("nocturne_of_shadow", "shadow", LEFT, RIGHT, RIGHT, A, LEFT, RIGHT, DOWN),
    PRELUDE_OF_LIGHT("prelude_of_light", "light", UP, RIGHT, UP, RIGHT, LEFT, UP);

    public final String id;
    public final String warpKey;
    public final OcarinaNote[] notes;

    OcarinaSong(String id, String warpKey, OcarinaNote... notes) {
        this.id = id;
        this.warpKey = warpKey;
        this.notes = notes;
    }

    /** Devuelve la canción que termina el historial de notas, priorizando las más largas. */
    public static OcarinaSong match(List<OcarinaNote> played) {
        OcarinaSong best = null;
        for (OcarinaSong s : values()) {
            int n = s.notes.length;
            if (played.size() < n) continue;
            boolean ok = true;
            for (int i = 0; i < n; i++) {
                if (played.get(played.size() - n + i) != s.notes[i]) { ok = false; break; }
            }
            if (ok && (best == null || n > best.notes.length)) best = s;
        }
        return best;
    }

    public static OcarinaSong byOrdinal(int i) {
        OcarinaSong[] v = values();
        return i < 0 || i >= v.length ? null : v[i];
    }
}
