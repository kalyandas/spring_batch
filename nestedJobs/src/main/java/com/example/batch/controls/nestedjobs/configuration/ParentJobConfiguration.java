package com.example.batch.controls.nestedjobs.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ParentJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Job childJob;

	@Autowired
	private JobLauncher jobLauncher;

	@Bean
	public Step step1Parent() {
		return stepBuilderFactory.get("step1Parent")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(String.format(">>Step 1 of Parent"));
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step step2Parent() {
		return stepBuilderFactory.get("step2Parent")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(String.format(">>Step 2 of Parent"));
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep"))
				.job(childJob)
				.launcher(jobLauncher)
				.repository(jobRepository)
				.transactionManager(transactionManager)
				.build();

		return jobBuilderFactory.get("parentJob")
				.start(step1Parent())
				.next(childJobStep)
				.next(step2Parent())
				.build();
	}
}
