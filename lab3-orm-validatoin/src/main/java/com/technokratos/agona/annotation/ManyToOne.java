package com.technokratos.agona.annotation;

import com.technokratos.agona.enums.FetchType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToOne {
    String to();
    FetchType fetchType() default FetchType.LAZY;
}
