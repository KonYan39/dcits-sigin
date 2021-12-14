package com.dcits.dx.card;

import com.dcits.dx.card.config.CardScheduSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Random;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class SignApplication {
	@Autowired
	private CardScheduSet cardScheduSet;

	public static void main(String[] args) {
		SpringApplication.run(SignApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Random random() {
		return new Random();
	}

	@PostConstruct
	public void init() {
		cardScheduSet.refreshOrInit(true);
	}
}
