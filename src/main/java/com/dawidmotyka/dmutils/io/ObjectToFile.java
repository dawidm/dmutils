/*
 * Copyright 2019 Dawid Motyka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dawidmotyka.dmutils.io;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectToFile {
    public static final Logger logger = Logger.getLogger(ObjectToFile.class.getName());
    public static class ObjectReadWriteException extends Exception {
        ObjectReadWriteException(String msg) {
            super(msg);
        }
    }
    public static void write(Object object, String filename) throws ObjectReadWriteException {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filename));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (IOException e) {
            logger.log(Level.WARNING,String.format("error writing object %s to file %s",object.getClass().getName(),filename),e);
            throw new ObjectReadWriteException(e.getLocalizedMessage());
        }
    }
    public static Object read(Class<? extends Object> objectClass, String filename) throws ObjectReadWriteException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename));
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            if (!object.getClass().equals(objectClass))
                logger.log(Level.WARNING,String.format("error reading object from file %s (wrong class)",filename));
            return object;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING,String.format("error reading object from file %s",filename),e);
            throw new ObjectReadWriteException(e.getLocalizedMessage());
        }
    }
}
