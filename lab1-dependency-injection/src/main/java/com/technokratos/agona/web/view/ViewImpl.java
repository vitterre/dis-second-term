package com.technokratos.agona.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;

import java.util.Map;

public class ViewImpl implements View {
    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        val keys = model.keySet();

        keys.forEach(k -> {
            try {
                val writer = response.getWriter();
                writer.write("<h1>%s</h1>".formatted(model.get(k)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
