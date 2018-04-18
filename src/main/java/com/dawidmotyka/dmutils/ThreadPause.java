package com.dawidmotyka.dmutils;

import java.util.logging.Logger;

public class ThreadPause {

    private static final Logger logger = Logger.getLogger(ThreadPause.class.getName());

    public static void millis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.warning("ThreadPause interrupted");
        }
    }

}
