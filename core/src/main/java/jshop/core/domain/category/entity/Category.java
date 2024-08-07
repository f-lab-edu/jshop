package jshop.core.domain.category.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import jshop.core.domain.category.dto.CreateCategoryRequest;
import jshop.core.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    /**
     * 카테고리는 자기 참조를 한다. 부모 카테고리가 삭제되면 자식 카테고리도 삭제.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * 카테고리는 자기 참조를 한다.
     */
    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @Column(unique = true)
    private String name;

    public static Category of(CreateCategoryRequest createCategoryRequest) {
        return Category
            .builder().name(createCategoryRequest.getName()).build();
    }
}
