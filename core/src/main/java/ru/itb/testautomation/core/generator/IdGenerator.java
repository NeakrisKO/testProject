package ru.itb.testautomation.core.generator;

import java.util.UUID;

public class IdGenerator {

    private static volatile IdGenerator instance;

    private IdGenerator(){}

    public static IdGenerator getInstance() {
        IdGenerator localInstance = instance;
        if (localInstance == null) {
            synchronized (IdGenerator.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new IdGenerator();
                }
            }
        }
        return localInstance;
    }

    public Object getId() {
        return UUID.randomUUID();
    }
}
