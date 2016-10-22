package com.exmple.batch.controls.listener.configuration;

import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kalyan on 17-10-2016.
 */
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
/*            return stepBuilderFactory.get("step1")
                    .tasklet(new Tasklet() {
                        @Override
                        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                            System.out.println("---In step 1");
                            return RepeatStatus.FINISHED;
                        }
                    })
                    .listener(new StepListener())
                    .build();*/
        }

        @Bean
        public Job listenerJob() {
            return jobBuilderFactory.get("listenerJob")
                    .start(step1())
                    .listener(jobExecutionListener())
                    .build();
        }

        @Bean
        public JobExecutionListener jobExecutionListener(){
            return new JobExecutionListener() {
                @Override
                public void beforeJob(JobExecution jobExecution) {
                    System.out.println(">> Before job:"+jobExecution);
                }

                @Override
                public void afterJob(JobExecution jobExecution) {
                    System.out.println("<< After job:"+jobExecution);
                }
            };
        }
    }
    class StepListener{
        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
            System.out.println(">> Before step");
        }
        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution) {
            System.out.println("<< After step");
            return null;
        }
    }

    class ChunkListener{
        @BeforeChunk
        public void beforeChunk(ChunkContext context) {
            System.out.println(">> Before the chunk");
        }

        @AfterChunk
        public void afterChunk(ChunkContext context) {
            System.out.println("<< After the chunk");
        }
    }
