package jshop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void test() throws Exception {
        System.out.println("test");

        ExecutorService executors = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            try {
                executors.submit(() -> {
                    RLock testLock = redissonClient.getLock("testLock");
                    try {
                        testLock.tryLock(100L, TimeUnit.MINUTES);
                        System.out.println(Thread.currentThread() + " get lock");
                        Thread.sleep((long) (Math.random() * 2000));
                    } catch (Exception e) {

                    } finally {

                        testLock.unlock();
                        System.out.println(Thread.currentThread() + " release lock");
                    }
                });
            } catch (Exception e) {

            }

        }

        executors.shutdown();
        executors.awaitTermination(3, TimeUnit.MINUTES);
    }
}
