package com.seamfix.bioweb.microservices.syncjob.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

@Slf4j
public final class WorkerUtil {

    private WorkerUtil() {
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public static Object convertFromBytes(byte[] bytes){
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(bis)) {
            return objectInputStream.readObject();
        } catch (Exception e) {
            log.error("convertFromBytes Exception", e);
        }

        return null;
    }
}
