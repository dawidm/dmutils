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
    public interface TaskFailedListener {
        public void taskFailed();
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries, TaskFailedListener taskFailedListener) {
        boolean success = false;
        boolean limitedRetries=true;
        if(maxRetries==0)
            limitedRetries=false;
        while(success==false) {
            try {
                task.run();
                break;
            } catch (Exception e) {
                onErrorListener.onError(e);
            }
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                onErrorListener.onError(e);
                break;
            }
            maxRetries--;
            if(limitedRetries && maxRetries<=0) {
                taskFailedListener.taskFailed();
                break;
            }
        }
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries) {
        planTask(task, onErrorListener, intervalMs, maxRetries, ()->{});
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs) {
        planTask(task,onErrorListener,intervalMs,0);
    }
}
