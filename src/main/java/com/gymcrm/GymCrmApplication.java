package com.gymcrm;

import com.gymcrm.config.WebAppInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.SpringServletContainerInitializer;

import java.io.File;
import java.util.Collections;

public class GymCrmApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymCrmApplication.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        logger.info("Starting GymCRM on port {}", PORT);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);

        // Tomcat needs a work directory for session persistence
        tomcat.setBaseDir(new File(System.getProperty("java.io.tmpdir"), "gymcrm-tomcat").getAbsolutePath());

        // getConnector() must be called explicitly before start().
        // Otherwise, no HTTP connector is created and the port is never bound.
        tomcat.getConnector();

        // Context path root
        Context ctx = tomcat.addContext("",
                new File(System.getProperty("java.io.tmpdir")).getAbsolutePath());

        //Manually invoking SpringServletContainerInitializer so WebAppInitializer is executed in embedded Tomcat
        ctx.addServletContainerInitializer(
                new SpringServletContainerInitializer(),
                Collections.singleton(WebAppInitializer.class)
        );

        tomcat.start();
        logger.info("GymCRM started — http://localhost:{}", PORT);

        // Block the main thread to let Tomcat run until the process is terminated.
        tomcat.getServer().await();
    }
}
