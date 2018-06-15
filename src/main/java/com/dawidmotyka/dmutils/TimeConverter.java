package com.dawidmotyka.dmutils;

public class TimeConverter {

    public static final int SECONDS_IN_MINUTE=60;
    public static final int SECONDS_IN_HOUR=SECONDS_IN_MINUTE*60;
    public static final int SECONDS_IN_DAY=SECONDS_IN_HOUR*24;

    public static String secondsToMinutesHoursDays(int seconds) {
        if(seconds>SECONDS_IN_DAY && seconds%SECONDS_IN_DAY==0)
            return seconds/SECONDS_IN_DAY+"d";
        if(seconds>SECONDS_IN_HOUR && seconds%SECONDS_IN_HOUR==0)
            return seconds/SECONDS_IN_HOUR+"h";
        if(seconds>SECONDS_IN_MINUTE && seconds%SECONDS_IN_MINUTE==0)
            return seconds/SECONDS_IN_MINUTE+"m";
        return seconds+"s";
    }
}
