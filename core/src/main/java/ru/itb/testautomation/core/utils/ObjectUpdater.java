package ru.itb.testautomation.core.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectUpdater {
    private Map<String, UpdaterItem> data = new LinkedHashMap<>();

    public ObjectUpdater() {
    }

    public void update(Object from, Object to) {
        try {
            for (Field field : to.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Parameter.class)) {
                    Parameter parameter = field.getAnnotation(Parameter.class);
                    if (!parameter.skipUpdate()) {
                        field.setAccessible(true);
                        data.put(parameter.shortName(), new UpdaterItem(parameter.shortName(), field.get(to)));
                    }

                }
            }
            for (Map.Entry<String, UpdaterItem> entry : data.entrySet()) {
                for (Field field : from.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Parameter.class)) {
                        Parameter parameter = field.getAnnotation(Parameter.class);
                        if (!parameter.skipUpdate() && parameter.shortName().equals(entry.getValue().getField())) {
                            field.setAccessible(true);
                            if (parameter.hasConverter()) {
                                Class _class = Class.forName(parameter.converter());
                                Converter converter = (Converter) _class.newInstance();
                                field.set(from, converter.convert((Collection<?>) data.get(parameter.shortName()).getValue()));
                            } else
                                field.set(from, entry.getValue().getValue());
                        }
                    }
                }

            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
