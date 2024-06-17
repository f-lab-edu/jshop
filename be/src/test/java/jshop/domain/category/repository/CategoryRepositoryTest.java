package jshop.domain.category.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import jshop.domain.address.repository.AddressRepository;
import jshop.domain.category.entity.Category;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void 카테고리_추가() {
        // given
        Category category = Category
            .builder().name("전자제품").build();

        // when
        categoryRepository.save(category);

        // then
        Optional<Category> findCategory = categoryRepository.findById(category.getId());
        assertThat(findCategory.get()).isEqualTo(category);
        assertThat(findCategory.get().getName()).isEqualTo("전자제품");
    }

}