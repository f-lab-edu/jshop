package jshop.domain.delivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jshop.domain.address.entity.Address;
import jshop.domain.order.entity.Order;
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
@Table(name = "delivery")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;
    

    /**
     * 주문을 fk로 보관.
     * 특별한거 없음.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(value = EnumType.STRING)
    private DeliveryState deliveryState;

    @Column(name = "receiver_name")
    private String receiverName;
    @Column(name = "receiver_number")
    private String receiverNumber;
    private String province;
    private String city;
    private String district;
    private String street;

    @Column(name = "detail_address1", nullable = true)
    private String detailAddress1;

    @Column(name = "detail_address2", nullable = true)
    private String detailAddress2;

    @Column(nullable = true)
    private String message;
}
