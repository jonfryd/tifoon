package com.elixlogic.tifoon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TifoonApp {
    public static void main(String[] args) {
        SpringApplication.run(TifoonApp.class, args);
    }
}
