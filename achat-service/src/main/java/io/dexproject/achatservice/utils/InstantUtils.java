package com.dexproject.shop.api.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class InstantUtils {
    private static Instant now() {
    	return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static Date dateNow() {
    	return Date.from(now());
    }
}