package com.mycompany.covid19Batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Covid19BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(Covid19BatchApplication.class, args);
	}

}
