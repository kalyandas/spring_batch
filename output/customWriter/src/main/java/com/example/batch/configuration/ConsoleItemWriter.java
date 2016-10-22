package com.example.batch.configuration;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class ConsoleItemWriter implements ItemWriter<String> {

	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("Writing chunk: " + items);

		for (String item : items) {
			System.out.println(">> " + item);
		}
	}
}
