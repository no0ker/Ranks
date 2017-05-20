package com.github.no0ker.ranks;

import java.time.Clock;

public class TimeHelper {
    public static Long getCurrentSeconds(){
        return Clock.systemDefaultZone().instant().getEpochSecond();
    }
}
