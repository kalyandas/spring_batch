package com.example.batch.configuration;

import java.util.ArrayList;
import java.util.List;

import com.example.batch.components.CustomException;
import com.example.batch.components.CustomSkipListener;
import com.example.batch.components.SkipItemProcessor;
import com.example.batch.components.SkipItemWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public ListItemReader<String> reader() {

		List<String> items = new ArrayList<>();

		for(int i = 0; i < 100; i++) {
			items.add(String.valueOf(i));
		}

		return new ListItemReader<>(items);
	}

	@Bean
	@StepScope
	public SkipItemProcessor processor() {
		return new SkipItemProcessor();
	}

	@Bean
	@StepScope
	public SkipItemWriter writer() {
		return new SkipItemWriter();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step")
				.<String, String>chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.faultTolerant()
				.skip(CustomException.class)
				.skipLimit(15)
				.listener(new CustomSkipListener())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}
}
