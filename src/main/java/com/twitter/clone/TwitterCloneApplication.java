package com.twitter.clone;

import com.twitter.clone.service.UserService;
import com.twitter.clone.service.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TwitterCloneApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TwitterCloneApplication.class, args);
	}

}
