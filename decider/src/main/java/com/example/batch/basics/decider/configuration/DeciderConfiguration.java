package com.example.batch.basics.decider.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class DeciderConfiguration {

    private static Random random = new Random();
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> Step 1");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step trueStep() {
		return stepBuilderFactory.get("trueStep")
				.tasklet((contribution, chunkContext) -> {
					System.out.println(">> True Step");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step falseStep() {
		return stepBuilderFactory.get("falseStep")
				.tasklet((contribution, chunkContext) -> {
                    System.out.println(">> False Step");
					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public JobExecutionDecider decider() {
        return (new JobExecutionDecider() {
			@Override
			public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
				//boolean flag = random.nextBoolean();
				int flag = random.nextInt(10);
				System.out.println(String.format(">> FLAG VALUE in DECIDER: %d", flag));
				//return  new FlowExecutionStatus("FALSE");
				//return flag? new FlowExecutionStatus("TRUE") : new FlowExecutionStatus("FALSE") ;
				return flag > 8 ? new FlowExecutionStatus("TRUE") : new FlowExecutionStatus("FALSE");
			}
		});
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.next(decider())
				.from(decider()).on("FALSE").to(falseStep())
				.from(decider()).on("TRUE").to(trueStep())
				.from(falseStep()).on("*").to(decider())
				.end()
				.build();
	}
}
