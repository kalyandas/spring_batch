package com.example.batch.writer;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by kalyan on 17-10-2016.
 */
public class ConsoleItemWriter implements ItemWriter<String>{
    @Override
    public void write(List<? extends String> list) throws Exception {
        System.out.println("Writing chunk "+list);
        list.forEach(item -> System.out.println(item));
    }
}
