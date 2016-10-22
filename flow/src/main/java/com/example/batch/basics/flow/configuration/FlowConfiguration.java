package com.example.batch.basics.flow.configuration;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kalyan on 16-10-2016.
 */
@Configuration
public class FlowConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1").tasklet((contribution,chunkContext) -> {
                        System.out.println(">> Step 1");
                        return RepeatStatus.FINISHED;
                    }
        ).build();
    }
    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2").tasklet((contribution,chunkContext) -> {
                    System.out.println(">> Step 2");
                    return RepeatStatus.FINISHED;
                }
        ).build();
    }

    @Bean
    public Flow flow(){
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        flowBuilder.start(step1())
                .next(step2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step customStep() {
        return stepBuilderFactory.get("customStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(">> Custom Step");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

/*	@Bean
	public Job flowAtFirstJob(Flow flow) {
        System.out.println("<<Flow at first>>");
		return jobBuilderFactory.get("flowAtFirstJob")
				.start(flow)
				.next(customStep())
				.end()
				.build();
	}*/
    @Bean
    public Job flowAtLastJob(Flow flow) {
        System.out.println("<<Flow not at first>>");
        return jobBuilderFactory.get("flowAtLastJob")
                .start(customStep())
                .on(ExitStatus.COMPLETED.getExitCode()).to(flow)//.next(flow) does not work
                .end()
                .build();
    }
}
