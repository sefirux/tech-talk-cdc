package com.jpaz.operations;

import io.micronaut.runtime.Micronaut;

import java.util.TimeZone;

public class OperationsServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        Micronaut.run(OperationsServiceApplication.class, args);
    }
}
