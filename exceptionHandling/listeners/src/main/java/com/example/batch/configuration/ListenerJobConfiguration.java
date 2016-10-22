package com.example.batch.configuration;

import java.util.Arrays;
import java.util.List;

import com.example.batch.listener.ChunkListener;
import com.example.batch.listener.JobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class ListenerJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public ItemReader<String> reader() {
		return new ListItemReader<>(Arrays.asList("one", "two", "three"));
	}

	@Bean
	public ItemWriter<String> writer() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					System.out.println("Writing item " + item);
				}
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(2)
				.faultTolerant()
				.listener(new ChunkListener())
				.reader(reader())
				.writer(writer())
				.build();
	}

	@Bean
	public Job listenerJob(JavaMailSender javaMailSender) {
		return jobBuilderFactory.get("listenerJob")
				.start(step1())
				.listener(new JobListener(javaMailSender))
				.build();
	}
}
