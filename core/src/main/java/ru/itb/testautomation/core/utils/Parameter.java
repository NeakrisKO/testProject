package ru.itb.testautomation.core.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    String shortName();
    boolean skipUpdate() default false;
    boolean hasConverter() default false;
    String converter() default "simple";
}
