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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jshop.domain.category.entity.Category;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.user.entity.User;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Slf4j
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

    private String manufacturer;

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

    public static Product of(CreateProductRequest createProductRequest, Category category, User user) {
        return Product
            .builder()
            .name(createProductRequest.getName())
            .manufacturer(createProductRequest.getManufacturer())
            .description(createProductRequest.getDescription())
            .category(category)
            .owner(user)
            .attributes(createProductRequest.getAttributes())
            .build();
    }

    /**
     * 상품에 정의된 속성과, 상세 상품의 속성을 비교합니다.
     * 상세 상품의 속성이 상품에 정의되어있지 않거나, 속성 값이 상품에 정의되어 있지 않다면 false
     *
     * @param detailAttribute
     *
     * @return
     */
    public boolean verifyChildAttribute(Map<String, String> detailAttribute) {
        if (attributes == null && detailAttribute == null) {
            return true;
        }

        Set<String> attributesKeySet = Optional.ofNullable(attributes).orElse(new HashMap<>()).keySet();
        Set<String> detailAttributeKeySet = Optional.ofNullable(detailAttribute).orElse(new HashMap<>()).keySet();

        if (!attributesKeySet.equals(detailAttributeKeySet)) {
            log.warn("상품의 속성과, 상세 상품의 속성이 일치하지 않습니다. 상품의 속성 : {}, 상세 상품의 속성 : {}", attributesKeySet,
                detailAttributeKeySet);
            return false;
        }

        for (String key : detailAttributeKeySet) {
            String attributeValue = detailAttribute.get(key);
            if (!attributes.get(key).contains(attributeValue)) {
                log.warn("상세 상품의 속성값이 상품에 정의되지 않았습니다. 상세 상품의 속성 : {}, 상품에 정의된 속성 : {}", attributeValue,
                    attributes.get(key));
                return false;
            }
        }
        return true;
    }


}
