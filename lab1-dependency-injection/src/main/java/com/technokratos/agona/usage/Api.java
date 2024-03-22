package com.technokratos.agona.usage;

import com.technokratos.agona.annotations.Component;
import com.technokratos.agona.annotations.Endpoint;
import com.technokratos.agona.annotations.Get;
import com.technokratos.agona.annotations.Wire;

@Endpoint(path = "/api")
@Component
public class Api {

    @Wire
    private Service service;

    @Get(path = "/hello")
    public String hello() {
        return service.hello();
    }
}
