package it.flipb.theapp;

import it.flipb.theapp.config.RootConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    private RootConfiguration configuration;
    
    @Autowired
    public Main(RootConfiguration configuration) {
        this.configuration = configuration;
        
         System.out.println(configuration.getApplication());
         System.out.println(configuration.getNetwork());
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
