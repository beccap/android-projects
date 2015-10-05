package com.beccap.lol_loader.util;

import java.util.Date;

/**
 * Created by beccap on 10/4/15.
 */
public class Random {
    private static java.util.Random random = new java.util.Random(new Date().getTime());

    public static int getInt(int range) {
        return random.nextInt(range);
    }
}
