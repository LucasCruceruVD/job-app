package com.vertical.jobapp;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.servlet.config.annotation.*;


@SpringBootApplication
@Configuration
@ComponentScan(basePackages = { "com.vertical.jobapp.*"})
@EnableJpaRepositories({ "com.vertical.jobapp.*"})
@EntityScan({ "com.vertical.jobapp.model"})
@EnableTransactionManagement
@EnableWebMvc
public class JobAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobAppApplication.class, args);
	}

}
