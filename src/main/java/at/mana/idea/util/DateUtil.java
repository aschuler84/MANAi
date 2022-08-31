/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class DateUtil {

    public static DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static Instant getInstantFromMicros(Long microsSinceEpoch) {
        return Instant.ofEpochSecond(TimeUnit.MICROSECONDS.toSeconds(microsSinceEpoch),
                TimeUnit.MICROSECONDS.toNanos( Math.floorMod( microsSinceEpoch, TimeUnit.SECONDS.toMicros(1))));
    }

    public static long getMicrosecondsSinceEpochFrom( Instant instant ) {
       return ChronoUnit.MICROS.between(java.time.Instant.EPOCH, instant);
    }
}
