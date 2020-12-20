/*
 * Copyright 2019 Dawid Motyka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dawidmotyka.dmutils.runtime;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dawid on 10/21/17.
 */
public class RepeatTillSuccess {
    
    public interface RunnableWithException {
        void run() throws Exception;
    }
    public interface OnErrorListener {
        void onError(Exception t);
    }
    public interface OnDoneListener {
        void onDone();
    }
    public interface TaskFailedListener {
        void taskFailed();
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture = null;
    private int numTries = 0;

    public void planTask(RunnableWithException task, OnDoneListener onDoneListener, OnErrorListener onErrorListener, int intervalMs, int maxRetries, TaskFailedListener taskFailedListener) {
        planTask(task, onDoneListener, onErrorListener, intervalMs, maxRetries, taskFailedListener, null);
    }

    // notCountExceptions - when these exception types occurs running the task, this is not counted for retry limit
    public void planTask(RunnableWithException task, OnDoneListener onDoneListener, OnErrorListener onErrorListener, int intervalMs, int maxRetries, TaskFailedListener taskFailedListener, Set<Class<? extends Exception>> notCountExceptions) {
        if (scheduledFuture != null)
            throw new IllegalStateException("Task already started");
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (maxRetries > 0 && numTries >= maxRetries) {
                    taskFailedListener.taskFailed();
                    cancelTask();
                } else {
                    numTries += 1;
                    task.run();
                    cancelTask();
                    onDoneListener.onDone();
                }
            } catch (Exception e) {
                onErrorListener.onError(e);
                if (notCountExceptions != null && notCountExceptions.contains(e.getClass()))
                    numTries -= 1;
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    private void cancelTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    public void planTask(RunnableWithException task, OnDoneListener onDoneListener, OnErrorListener onErrorListener, int intervalMs, int maxRetries) {
        planTask(task, onDoneListener, onErrorListener, intervalMs, maxRetries, ()->{});
    }

    public void planTask(RunnableWithException task, OnDoneListener onDoneListener, OnErrorListener onErrorListener, int intervalMs) {
        planTask(task, onDoneListener, onErrorListener,intervalMs,0);
    }
}
