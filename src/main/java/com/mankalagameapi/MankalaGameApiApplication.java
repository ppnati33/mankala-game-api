package com.mankalagameapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
//@EnableDiscoveryClient
@SpringBootApplication
public class MankalaGameApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MankalaGameApiApplication.class, args);
    }

}
