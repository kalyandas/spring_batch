
package com.example.batch.controls.nestedjobs.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ChildJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1Child() {
		return stepBuilderFactory.get("step1Child")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("\t>>Step 1 at Child");
					return RepeatStatus.FINISHED;
				}).build();
	}
	@Bean
	public Step step2Child() {
		return stepBuilderFactory.get("step2Child")
				.tasklet((contribution, chunkContext) -> {
					System.out.println("\t>>Step 2 at Child");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Job childJob() {
        System.out.println(step1Child());
        return jobBuilderFactory.get("childJob")
				.start(step1Child())
				.next(step2Child())
				.build();
	}
}
