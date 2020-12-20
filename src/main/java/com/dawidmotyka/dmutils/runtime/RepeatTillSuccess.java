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
    public interface TaskFailedListener {
        void taskFailed();
    }

    // retryExceptionFilters - if exception from the list occur when executing task, this is not counted to max retries limit
    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries, TaskFailedListener taskFailedListener, Set<Class<? extends Exception>> retryExceptionFilters) {
        boolean limitedRetries=true;
        if(maxRetries == 0)
            limitedRetries=false;
        while(true) {
            try {
                task.run();
                break;
            } catch (Exception e) {
                onErrorListener.onError(e);
                if (retryExceptionFilters != null && retryExceptionFilters.contains(e.getClass()))
                    maxRetries++;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(intervalMs);
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

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries, TaskFailedListener taskFailedListener) {
        planTask(task, onErrorListener, intervalMs, maxRetries, ()->{}, null);
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs, int maxRetries) {
        planTask(task, onErrorListener, intervalMs, maxRetries, ()->{});
    }

    public static void planTask(RunnableWithException task, OnErrorListener onErrorListener, int intervalMs) {
        planTask(task,onErrorListener,intervalMs,0);
    }
}
