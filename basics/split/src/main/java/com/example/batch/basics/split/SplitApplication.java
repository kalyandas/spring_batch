package com.example.batch.basics.split;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kalyan on 16-10-2016.
 */
@SpringBootApplication
@EnableBatchProcessing
public class SplitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SplitApplication.class, args);
    }
}
