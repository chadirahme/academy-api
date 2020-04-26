package com.academy.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Configuration
@RestController
public class AmericanacademyApplication {

	private static final Logger log = LoggerFactory.getLogger(AmericanacademyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AmericanacademyApplication.class, args);
	}
	@RequestMapping("/paper")
	public String index() {
		//log.warn("I'm so tired to welcome everyone", new Exception(new Exception()));
		return "Hello world!";
	}
}
