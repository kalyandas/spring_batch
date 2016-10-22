package com.example.batch.configuration;

import java.util.ArrayList;
import java.util.List;

import com.example.batch.reader.ListItemReader;

import com.example.batch.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
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
	public ListItemReader listItemReader() {
		List<String> data = new ArrayList<>();
		data.add("Item 1");
		data.add("Item 2");
		data.add("Item 3");
        data.add("Item 4");
        data.add("Item 5");
        data.add("Item 6");
        data.add("Item 7");
		return new ListItemReader(data);
	}

	@Bean
	public ConsoleItemWriter consoleItemWriter(){
		return new ConsoleItemWriter();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<String, String>chunk(3)
				.reader(listItemReader())
				.writer(/*new ItemWriter<String>() {
					@Override
					public void write(List<? extends String> list) throws Exception {
						for (String curItem : list) {
							System.out.println("curItem = " + curItem);
						}
					}
				}*/
				consoleItemWriter()).build();
	}

	@Bean
	public Job customIOJob() {
		return jobBuilderFactory.get("customIOJob")
				.start(step1())
				.build();
	}
}
