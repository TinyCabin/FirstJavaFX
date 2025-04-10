package com.example.AutkaFX;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CSVColumn {
    String name(); // Nazwa kolumny
    int order();  // Kolejność kolumny
}

