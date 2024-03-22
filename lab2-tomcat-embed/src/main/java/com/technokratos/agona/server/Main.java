package com.technokratos.agona.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        // setting up dependencies
        val tomcat = new Tomcat();
        tomcat.setBaseDir("temp");

        val connector = new Connector();
        connector.setPort(1337);
        tomcat.setConnector(connector);

        val contextPath = "";
        val docBase = new File(".").getAbsolutePath();
        val tomcatContext = tomcat.addContext(contextPath, docBase);

        // creating a servlet
        val servlet = new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req,
                    HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("text/html; charset=utf-8");
                val writer = resp.getWriter();
                writer.println("<html><head><meta charset='utf-8'/><title>Embedded Tomcat</title></head><body>");
                writer.println("<h1>Мы встроили Tomcat в свое приложение!</h1>");

                writer.println("<div>Метод: " + req.getMethod() + "</div>");
                writer.println("<div>Ресурс: " + req.getPathInfo() + "</div>");
                writer.println("</body></html>");
            }
        };

        // including a servlet
        val servletName = "dispatcherServlet";
        tomcat.addServlet(contextPath, servletName, servlet);

        tomcatContext.addServletMappingDecoded("/*", servletName);

        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}