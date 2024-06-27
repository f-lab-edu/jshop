package jshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing
public class JshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JshopApplication.class, args);
    }
}