
package com.example.batch.basics.split.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
/**
 * Created by kalyan on 16-10-2016.
 */
@Configuration
public class SplitConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Tasklet tasklet() {
        return (new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println(String.format("%s on thread %s", chunkContext.getStepContext().getStepName(),
						Thread.currentThread().getName()));
				return RepeatStatus.FINISHED;
			}
		});
	}

	@Bean
    public Step step1(){
        return stepBuilderFactory.get("step1").tasklet(tasklet()).build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2").tasklet(tasklet()).build();
    }
    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3").tasklet(tasklet()).build();
    }

	@Bean
	public Flow flow1() {
		return new FlowBuilder<Flow>("flow1")
				.start(step1())
				.build();
	}

	@Bean
	public Flow flow2() {
		return new FlowBuilder<Flow>("flow2")
				.start(step2())
				.next(step3())
				.build();
	}

	@Bean
	public Job splitJobWithFlow() {
		return jobBuilderFactory.get("splitJobWithFlow")
				.start(flow1())
				.split(new SimpleAsyncTaskExecutor()).add(flow2())
				.end()
				.build();
	}
}
