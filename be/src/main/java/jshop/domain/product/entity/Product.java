package jshop.domain.product.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jshop.domain.category.entity.Category;
import jshop.domain.manufacturer.entity.Manufacturer;
import jshop.domain.user.entity.User;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    /**
     * 사용자는 여러개의 상품을 등록할 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * 상품은 하나의 카테고리를 갖는다.
     * 카테고리 하나에 여러개의 상품이 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 상품하나당 하나의 제조사가 있다.
     * 제조사는 여러개의 상품을 갖는다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    /**
     * 상품은 여러개의 detail 상품을 갖는다.
     * ex)
     * product : 아이폰 15
     * productDetail : 아이폰 15 : {128GB, 화이트}
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Default
    private final List<ProductDetail> productDetails = new ArrayList<>();

    private String name;

    @Lob
    private String description;

    /**
     * 상품 속성은 json으로 관리한다
     * attributes에는 productDetail 가 가질 수 있는 모든 속성 정보가 있다.
     * ex)
     * color : ["red", "white"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, List<String>> attributes;
}
