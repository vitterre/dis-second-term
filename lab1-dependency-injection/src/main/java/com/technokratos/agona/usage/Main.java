package com.technokratos.agona.usage;

import com.technokratos.agona.container.Context;
import com.technokratos.agona.web.ServletMapper;
import lombok.val;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        val tomcat = new Tomcat();
        val connector = new Connector();
        val applicationContextPath = "";
        val pwd = System.getProperty("user.dir");
        val servletMapperName = "servletMapper";

        tomcat.setBaseDir("TMC");
        connector.setPort(8080);
        tomcat.setConnector(connector);

        val tomcatContext = tomcat.addContext(applicationContextPath, pwd);
        val packageScan = "com.technokratos.agona.usage";

        val context = new Context(packageScan);
        tomcat.addServlet(applicationContextPath, servletMapperName, new ServletMapper(context));
        tomcatContext.addServletMappingDecoded("/*", servletMapperName);


        try {
            context.componentScan();
            context.setupComponents();

            System.out.println(context.getComponentContainer());
            System.out.println(context.getEndpointsContrainer());

            tomcat.start();
//            tomcat.getServer().await();
//
//            context.clear();
//
//            tomcat.stop();
//            tomcat.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
