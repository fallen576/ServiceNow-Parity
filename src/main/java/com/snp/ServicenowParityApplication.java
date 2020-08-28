package com.snp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
@EntityScan(basePackages = {"com.snp.entity"})
public class ServicenowParityApplication extends SpringBootServletInitializer {
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServicenowParityApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(ServicenowParityApplication.class, args);
	}
}
