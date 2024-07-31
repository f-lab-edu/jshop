package jshop;

import java.util.List;
import javax.sql.DataSource;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
public class ContainerTest {

    @Autowired
    DataSource dataSource;
//    @Container
//    private static MySQLContainer<?> container = new MySQLContainer<>("mysql:8.0")
//        .withDatabaseName("jshop_test")
//        .withUsername("root")
//        .withPassword("1234")
//        .withEnv("MYSQL_ROOT_PASSWORD", "1234");

    @Autowired
    private UserRepository userRepository;

//    @DynamicPropertySource
//    static void configureProperties(final DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> "jdbc:tc:mysql:8.0:///jshop_test");
//        registry.add("spring.datasource.username", container::getUsername);
//        registry.add("spring.datasource.password", container::getPassword);
//        registry.add("spring.datasource.driver-class-name", () -> "org.testcontainers.jdbc.ContainerDatabaseDriver");
//    }

    @Test
    public void test() throws Exception {
        System.out.println(dataSource.getConnection().getMetaData().getURL());
        User user = User
            .builder()
            .username("kim")
            .build();

        userRepository.save(user);
        List<User> users = userRepository.findAll();

        System.out.println(users);
    }
}
