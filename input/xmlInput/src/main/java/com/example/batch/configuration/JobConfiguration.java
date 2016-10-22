package com.example.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import com.example.batch.domain.Address;
import com.example.batch.domain.Customer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public StaxEventItemReader<Customer> customerItemReader() {

		XStreamMarshaller unmarshaller = new XStreamMarshaller();

		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer.class);
		aliases.put("address", Address.class);
		unmarshaller.setAliases(aliases);

		StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();

		reader.setResource(new ClassPathResource("/data/customers.xml"));
		reader.setFragmentRootElementName("customer");
		reader.setUnmarshaller(unmarshaller);

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
				.reader(customerItemReader())
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
