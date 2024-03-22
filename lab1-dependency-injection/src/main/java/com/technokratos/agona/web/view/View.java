package com.technokratos.agona.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface View {
    String getContentType();
    void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response);
}
