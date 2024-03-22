package com.technokratos.agona.controller;

import com.technokratos.agona.annotations.Controller;
import com.technokratos.agona.annotations.GetRequest;
import com.technokratos.agona.annotations.PostRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ApplicationController {

    @GetRequest(path="/app")
    public String getAppGet(HttpServletRequest request, HttpServletResponse response) {
        return "Hello!";
    }

    @PostRequest(path="/app")
    public String getAppPost(HttpServletRequest request, HttpServletResponse response) {
        return "Hello!";
    }
}
