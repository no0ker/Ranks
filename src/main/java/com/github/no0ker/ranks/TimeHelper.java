package com.github.no0ker.ranks;

import java.util.Date;

public class TimeHelper {
    public static Long getCurrentSeconds(){
        return new Date().getTime()/1000;
    }
}
