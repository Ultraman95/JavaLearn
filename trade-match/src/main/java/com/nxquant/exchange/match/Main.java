package com.nxquant.exchange.match;


import com.nxquant.exchange.match.core.MainWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Component
    public static class ApplicationDomain implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowCredentials(true)
                    .allowedHeaders("*")
                    .allowedOrigins("*")
                    .allowedMethods("*");
        }
    }


    @Component
    public static class ApplicationStarter implements ApplicationRunner {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Autowired
        MainWorker mainWorker;

        @Override
        public void run(ApplicationArguments args) {
            try {
                Thread manageThread = new Thread(mainWorker::start);
                manageThread.setDaemon(true);
                manageThread.setName("Match");
                manageThread.start();
            } catch (Exception exp) {
                logger.error("Match_ERROR: New MainWorker Failure !", exp);
                System.exit(-1);
            }
        }
    }
}