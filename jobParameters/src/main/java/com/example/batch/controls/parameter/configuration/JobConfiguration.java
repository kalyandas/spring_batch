
package com.example.batch.controls.parameter.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    // tells spring to instantiate each time a step is executed within a scope
    //unlike its not a singleton
    //instantiated lazily
	public Tasklet helloWorldTasklet(@Value("#{jobParameters['message']}") String message) {
		return (stepContribution, chunkContext) -> {
			System.out.println(message);
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet(helloWorldTasklet(null)) // setting a proxy to satisfy dependency
				.build();
	}

	@Bean
	public Job jobParametersJob() {
		return jobBuilderFactory.get("jobParametersJob")
				.start(step1())
				.build();
	}
}
