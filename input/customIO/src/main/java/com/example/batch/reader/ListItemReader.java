package com.example.batch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;

public class ListItemReader implements ItemReader<String> {

	private final Iterator<String> data;

	public ListItemReader(List<String> data) {
		this.data = data.iterator();
	}

	@Override
	public String read() throws Exception {
		String item = null;
		if(this.data.hasNext()) {
			item = this.data.next();
		}
		System.out.println("Reading "+item);
		return null;

	}
}
