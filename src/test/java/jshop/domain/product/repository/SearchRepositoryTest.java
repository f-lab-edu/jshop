package jshop.domain.product.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import jshop.domain.product.entity.ProductDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SearchRepositoryTest {

    @Autowired
    ProductDetailRepository productDetailRepository;

    @Autowired
    SearchRepository searchRepository;

    @Test
    public void test() {
        // given
        ProductDetail pd = ProductDetail
            .builder().build();

        productDetailRepository.save(pd);
        // when

        // then

        List<ProductDetail> search = searchRepository.search();
        System.out.println(search);
    }
}