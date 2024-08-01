package jshop.utils.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestContainers {
    
    static final MySQLContainer<?> mysqlContainer;

    static final RedisContainer redisContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("jshop_test")
            .withEnv("MYSQL_ROOT_PASSWORD", "1234");
        redisContainer = new RedisContainer("redis:7.2.5");

        mysqlContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    public static void init(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);
    }
}
