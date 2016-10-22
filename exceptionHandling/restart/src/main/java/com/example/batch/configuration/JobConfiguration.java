package com.example.batch.configuration;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
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
	public Tasklet restartTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
				System.out.println("STEP:"+chunkContext.getStepContext().getStepName());
				if(stepExecutionContext.containsKey("ran")) {
					System.out.println("Restarting again...");
					return RepeatStatus.FINISHED;
				}
				else {
					System.out.println("First time run.. setting ran=true");
					chunkContext.getStepContext().getStepExecution().getExecutionContext().put("ran", true);
					throw new RuntimeException("Deliberately throwing runtime exception !!!");
				}
			}
		};
	}

	@Bean
	public Step step1() {
		System.out.println("In step 1");
		return stepBuilderFactory.get("step1")
				.tasklet(restartTasklet())
				.build();
	}

	@Bean
	public Step step2() {
		System.out.println("In step 2");
		return stepBuilderFactory.get("step2")
				.tasklet(restartTasklet())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.next(step2())
				.build();
	}
}
