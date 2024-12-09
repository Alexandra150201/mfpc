package com.example.rezervari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RezervariApplication {
    public static void main(String[] args) {
        SpringApplication.run(RezervariApplication.class, args);
    }
}
