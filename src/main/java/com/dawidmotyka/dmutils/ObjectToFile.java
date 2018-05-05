package com.dawidmotyka.dmutils;

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
