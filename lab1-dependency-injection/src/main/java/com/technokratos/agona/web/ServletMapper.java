package com.technokratos.agona.web;

import com.technokratos.agona.annotations.Endpoint;
import com.technokratos.agona.annotations.Get;
import com.technokratos.agona.container.Context;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "servletMapper", urlPatterns = "/*")
@RequiredArgsConstructor
public class ServletMapper extends HttpServlet {


    private final Context context;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        val method = req.getMethod();
        val URI = req.getRequestURI();

        processRequest(method, URI, req, resp);
    }

    public void processRequest(final String method, final String uri, HttpServletRequest req, HttpServletResponse resp) {
        System.out.println(uri);
        if (method.equals("GET")) {
            processGet(uri, req, resp);
        }
    }

    private void processGet(final String uri, HttpServletRequest req, HttpServletResponse resp) {
        this.context.getEndpointsContrainer().keySet()
                .forEach(e -> {
                    val methods = List.of(e.getDeclaredMethods());

                    methods.forEach(m -> {
                        m.setAccessible(true);
                        if (m.isAnnotationPresent(Get.class)) {
                            val methodAnnotation = m.getAnnotation(Get.class);
                            val endPointAnnotation = e.getAnnotation(Endpoint.class);
                            val path = endPointAnnotation.path() + methodAnnotation.path();

                            if (uri.equals(path)) {
                                try {
                                    val result = m.invoke(this.context.getEndpointsContrainer().get(e));
                                    resp.setContentType("text/plain");
                                    resp.getWriter().write(result.toString());
                                } catch (Exception exception) {
                                    throw new RuntimeException(exception);
                                }
                            }
                        }
                    });

                });
    }
}
