package com.example.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class XmlWriterApplication {

	public static void main(String[] args) {
		SpringApplication.run(XmlWriterApplication.class, args);
	}
}
