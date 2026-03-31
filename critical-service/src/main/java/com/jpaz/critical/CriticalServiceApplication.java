package com.jpaz.critical;

import io.micronaut.runtime.Micronaut;

import java.util.TimeZone;

public class CriticalServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        Micronaut.run(CriticalServiceApplication.class, args);
    }
}
