/*
 * Copyright 2020 Dawid Motyka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dawidmotyka.dmutils.runtime;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepeatTillSuccessTest {

    Logger logger = Logger.getLogger(RepeatTillSuccessTest.class.getName());

    class ErrorsThenSuccess {

        private final int maxNumErrors;
        private int numErrors = 0;

        ErrorsThenSuccess(int maxNumErrors) {
            this.maxNumErrors = maxNumErrors;
        }

        public void simulateTask() throws Exception {
            logger.info("Simulating task");
            if (numErrors<maxNumErrors) {
                numErrors++;
                throw new Exception("error");
            }
        }

    }

    @Test
    void testOnErrorAndDone() {
        int plannedNumErrors = 3;
        RepeatTillSuccess repeatTillSuccess = new RepeatTillSuccess();
        ErrorsThenSuccess ets = new ErrorsThenSuccess(plannedNumErrors);
        final AtomicInteger numDone = new AtomicInteger(0);
        final AtomicInteger numFailed = new AtomicInteger(0);
        final AtomicInteger numErrors = new AtomicInteger(0);
        repeatTillSuccess.planTask(ets::simulateTask,
                numDone::incrementAndGet,
                (e)->numErrors.incrementAndGet(),
                1000,
                4,
                numFailed::incrementAndGet);
        try {
            Thread.sleep(3300);
        } catch (InterruptedException e) { throw new Error(); }
        assertEquals(numDone.get(),1);
        assertEquals(numFailed.get(), 0);
        assertEquals(numErrors.get(), plannedNumErrors);

    }
    @Test
    void testOnErrorAndFailed() {
        int plannedNumErrors = 4;
        RepeatTillSuccess repeatTillSuccess = new RepeatTillSuccess();
        ErrorsThenSuccess ets = new ErrorsThenSuccess(plannedNumErrors);
        final AtomicInteger numDone = new AtomicInteger(0);
        final AtomicInteger numFailed = new AtomicInteger(0);
        final AtomicInteger numErrors = new AtomicInteger(0);
        repeatTillSuccess.planTask(ets::simulateTask,
                numDone::incrementAndGet,
                (e)->numErrors.incrementAndGet(),
                1000,
                4,
                numFailed::incrementAndGet);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) { throw new Error(); }
        assertEquals(numDone.get(),0);
        assertEquals(numFailed.get(),1);
        assertEquals(numErrors.get(), plannedNumErrors);
    }
    @Test
    void testDone() {
        int plannedNumErrors = 0;
        RepeatTillSuccess repeatTillSuccess = new RepeatTillSuccess();
        ErrorsThenSuccess ets = new ErrorsThenSuccess(plannedNumErrors);
        final AtomicInteger numDone = new AtomicInteger(0);
        final AtomicInteger numFailed = new AtomicInteger(0);
        final AtomicInteger numErrors = new AtomicInteger(0);
        repeatTillSuccess.planTask(ets::simulateTask,
                numDone::incrementAndGet,
                (e)->numErrors.incrementAndGet(),
                1000,
                4,
                numFailed::incrementAndGet);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { throw new Error(); }
        assertEquals(numDone.get(),1);
        assertEquals(numFailed.get(), 0);
        assertEquals(numErrors.get(), 0);
    }
    @Test
    void testFailed() {
        int plannedNumErrors = 1;
        RepeatTillSuccess repeatTillSuccess = new RepeatTillSuccess();
        ErrorsThenSuccess ets = new ErrorsThenSuccess(plannedNumErrors);
        final AtomicInteger numDone = new AtomicInteger(0);
        final AtomicInteger numFailed = new AtomicInteger(0);
        final AtomicInteger numErrors = new AtomicInteger(0);
        repeatTillSuccess.planTask(ets::simulateTask,
                numDone::incrementAndGet,
                (e)->numErrors.incrementAndGet(),
                1000,
                1,
                numFailed::incrementAndGet);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { throw new Error(); }
        assertEquals(numDone.get(),0);
        assertEquals(numFailed.get(), 1);
        assertEquals(numErrors.get(), 1);
    }
}