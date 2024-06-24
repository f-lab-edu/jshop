package jshop.domain.category.repository;

import java.util.List;
import jshop.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findAll();

    public Category save(Category category);

    boolean existsByName(String name);

    Category findByName(String name);
}
