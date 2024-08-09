package jshop.core.domain.category.repository;

import java.util.List;
import java.util.Optional;
import jshop.core.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAll();

    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}
