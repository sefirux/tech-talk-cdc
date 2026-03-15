package com.jpaz.portfolio;

import io.micronaut.runtime.Micronaut;

import java.util.TimeZone;

public class OperationsApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        Micronaut.run(OperationsApplication.class, args);
    }
}
