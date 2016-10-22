package com.example.batch.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import com.example.batch.domain.Customer;
import com.example.batch.domain.CustomerRowMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(1000);
		reader.setRowMapper(new CustomerRowMapper());

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, firstName, lastName, birthdate");
		queryProvider.setFromClause("from customer");

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);

		return reader;
	}

	@Bean
	public ItemProcessor itemProcessor() {
		return new ItemProcessor<Customer, Customer>() {
			@Override
			public Customer process(Customer item) throws Exception {
				//Thread.sleep(new Random().nextInt(10));// simulating process delay
				Thread.sleep(5);// simulating process delay
				return new Customer(item.getId(),
						item.getFirstName().toUpperCase(),
						item.getLastName().toUpperCase(),
						item.getBirthdate());
			}
		};
	}

	@Bean
	public AsyncItemProcessor asyncItemProcessor() throws Exception {
		AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor();

		asyncItemProcessor.setDelegate(itemProcessor());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
		asyncItemProcessor.afterPropertiesSet();

		return asyncItemProcessor;
	}

	@Bean
	public JdbcBatchItemWriter customerItemWriter() {
		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	@Bean
	public AsyncItemWriter asyncItemWriter() throws Exception {
		AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();

		asyncItemWriter.setDelegate(customerItemWriter());
		asyncItemWriter.afterPropertiesSet();

		return asyncItemWriter;
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.chunk(1000)
				.reader(pagingItemReader())
/*				.processor(itemProcessor())
				.writer(customerItemWriter())*/
				.processor(asyncItemProcessor())
				.writer(asyncItemWriter())
				.build();
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}
}
