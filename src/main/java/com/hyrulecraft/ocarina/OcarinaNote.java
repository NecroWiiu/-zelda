package com.hyrulecraft.ocarina;

/** Notas de la ocarina: botón A y los cuatro botones C. */
public enum OcarinaNote {
    A, DOWN, RIGHT, LEFT, UP;

    public static OcarinaNote of(int i) { return values()[Math.floorMod(i, 5)]; }
}
