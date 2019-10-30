package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogParserServerApplication {

    public static void main(String[] args) {
        //todo group by method
        //todo change content method
        //todo use reflection instead of compile new class
        //todo optimise public List<LogDetail> parseLog()
        //todo optimise private String generateLogBlock(LogDetail logDetail)
        SpringApplication.run(LogParserServerApplication.class, args);
    }

}
