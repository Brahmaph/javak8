package com.demo.k8sdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class K8sDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sDemoApplication.class, args);
    }

    @GetMapping("/api/status")
    public String status() {
        return "Application is running successfully!";
    }

    @GetMapping("/api/message")
    public String message() {
        return "Java + Docker + Kubernetes deployment is working!";
    }
}