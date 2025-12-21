package com.example.sideproject01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

//resources 에 작성한 custom.properties 파일을 로딩하도록 한다.
@PropertySource(value="classpath:custom.properties")
@SpringBootApplication
@ConfigurationPropertiesScan
public class SideProject01BissolApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProject01BissolApplication.class, args);
	}

}
