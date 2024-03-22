package com.technokratos.agona.usage;

import com.technokratos.agona.annotations.Component;
import com.technokratos.agona.annotations.Wire;

@Component
public class Service {
    @Wire
    private Repository repository;

    public String hello() {
        return repository.doJob();
    }
}
