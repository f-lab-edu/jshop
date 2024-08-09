package jshop.core.domain.cart.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

@Controller
@RequiredArgsConstructor
public class TestRepository {

    private final EntityManager em;

    public void test() {

        em.flush();
        em.clear();
        System.out.println("teststest");
    }
}
