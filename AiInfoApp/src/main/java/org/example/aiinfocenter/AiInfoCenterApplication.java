package org.example.aiinfocenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiInfoCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiInfoCenterApplication.class, args);
        System.out.println("[http] http://localhost:8080");
    }
}
