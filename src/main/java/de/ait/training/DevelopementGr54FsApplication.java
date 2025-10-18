package de.ait.training;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DevelopementGr54FsApplication {
    public static void main(String[] args) {
        log.info("Starting DevelopmentGr54FsApplication");
        SpringApplication.run(DevelopementGr54FsApplication.class, args);
    }

}