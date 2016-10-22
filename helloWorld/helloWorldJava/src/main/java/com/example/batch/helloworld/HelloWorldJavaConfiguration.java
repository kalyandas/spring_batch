package com.example.batch.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by kalyan on 16-10-2016.
 */
@Configuration
//@ImportResource("classpath:context.xml")
@EnableBatchProcessing
public class HelloWorldJavaConfiguration {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean(name="helloWorldJavaTasklet")
    public Tasklet helloWorldJavaTasklet () {
        return new Tasklet() {
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello World !!! - Java");
                return RepeatStatus.FINISHED;
            }
        };
    }
    @Bean(name="step1")
    protected Step step1(@Qualifier("helloWorldJavaTasklet") Tasklet tasklet) {
        return steps.get("step1").tasklet(tasklet).build();
    }
    @Bean(name = "helloWorldJob")
    public Job helloJob(
            JobBuilderFactory jobs,
            @Qualifier("step1") Step step1
    ) throws Exception {
        return jobs.get("helloWorldJob")
                .start(step1)
                .build();
    }
}
