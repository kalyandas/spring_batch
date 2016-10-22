package com.example.batch.configuration;

import com.example.batch.domain.CustomerFieldSetMapper;
import com.example.batch.domain.Customer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("classpath*:/data/customer*.csv")
	private Resource[] inputFiles;

	@Bean
	public MultiResourceItemReader<Customer> multiResourceItemReader() {
		MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
		reader.setDelegate(customerItemReader());
		reader.setResources(inputFiles);
		return reader;
	}

	@Bean
	public FlatFileItemReader<Customer> customerItemReader() {
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();

		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});

		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
		customerLineMapper.afterPropertiesSet();

		reader.setLineMapper(customerLineMapper);

		return reader;
	}

	@Bean
	public ItemWriter<Customer> customerItemWriter() {
		return items -> {
			for (Customer item : items) {
				System.out.println(item.toString());
			}
		};
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<Customer, Customer>chunk(10)
				.reader(multiResourceItemReader())
				.writer(customerItemWriter())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}
}
