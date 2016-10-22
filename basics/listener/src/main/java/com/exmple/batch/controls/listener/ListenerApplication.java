package com.exmple.batch.controls.listener;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kalyan on 17-10-2016.
 */
@SpringBootApplication
@EnableBatchProcessing
public class ListenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListenerApplication.class, args);
    }

}
