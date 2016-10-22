package com.example.batch.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public ListItemReader itemReader() {
		List<String> items = new ArrayList<>(100);

		for(int i = 1; i <= 100; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader(items);
	}

	@Bean
	public ItemWriter itemWriter() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println(">> " + item);
				}
			}
		};
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(10)
				.reader(itemReader())
				.writer(itemWriter())
				.stream(itemReader())
				.build();
	}

	@Bean
	public Job listReadJob() {
		return jobBuilderFactory.get("listReadJob")
				.start(step())
				.build();
	}
}
