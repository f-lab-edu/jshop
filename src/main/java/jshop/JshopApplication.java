package jshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "jshop")
@EntityScan(basePackages = "jshop.core")
@EnableJpaRepositories(basePackages = "jshop.core")
public class JshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JshopApplication.class, args);
    }
}