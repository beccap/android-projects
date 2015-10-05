package com.beccap.lol_loader.util;

import java.util.Date;

/**
 * Random
 *
 * A static utility class that seeds the random generator and returns an int; could be expanded to
 * return longs and other types supported by the java Random utility
 */
public class Random {
    private static java.util.Random random = new java.util.Random(new Date().getTime());

    public static int getInt(int range) {
        return random.nextInt(range);
    }
}
