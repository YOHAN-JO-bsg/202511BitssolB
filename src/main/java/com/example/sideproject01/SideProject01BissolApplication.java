package com.example.sideproject01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:custom.properties")
@org.springframework.context.annotation.PropertySource(value = "classpath:custom.properties")
@SpringBootApplication
@ConfigurationPropertiesScan
public class SideProject01BissolApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProject01BissolApplication.class, args);
	}

}
