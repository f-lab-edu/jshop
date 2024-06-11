package jshop.domain.cart.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import jshop.domain.user.entity.User;
import jshop.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    /**
     * 유저하나당 하나의 카트를 갖는다. 유저가 삭제되면 카트도 삭제 -> 유저에 Cascade
     */
    @OneToOne(mappedBy = "cart", fetch = FetchType.LAZY)
    private User user;

    /**
     * 카트물품들은 카트가 삭제되면 같이 삭제
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE)
    private List<CartProductDetail> cartProductDetails = new ArrayList<>();
}
