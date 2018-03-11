package com.dawidmotyka.dmutils;

/**
 * Created by dawid on 10/21/17.
 */
public class RepeatTillSuccess {
    public interface RunnableWithException {
        public void run() throws Exception;
    }
    public interface OnErrorListener {
        public void onError(Exception t);
    }
    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries) {
        boolean success = false;
        boolean limitedRetries=true;
        if(maxRetries==0)
            limitedRetries=false;
        while(success==false) {
            try {
                task.run();
                success=true;
            } catch (Exception e) {
                onErrorListener.onError(e);
            } finally {
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    onErrorListener.onError(e);
                }
                maxRetries--;
                if(limitedRetries && maxRetries<=0)
                    success=true;
            }
        }
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs) {
        planTask(task,onErrorListener,intervalMs,0);
    }
}
