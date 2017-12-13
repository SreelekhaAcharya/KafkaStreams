package com.KStreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("com.KStreams.Data")
public class KStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KStreamsApplication.class, args);
	}
}
