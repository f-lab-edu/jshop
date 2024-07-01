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
import org.hibernate.envers.Audited;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery")
@Audited
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;


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

    public void cancel() {
        deliveryState = DeliveryState.CANCLED;
    }

    public static Delivery of(Address address) {
        return Delivery
            .builder()
            .deliveryState(DeliveryState.PREPARING)
            .receiverName(address.getReceiverName())
            .receiverNumber(address.getReceiverNumber())
            .province(address.getProvince())
            .city(address.getCity())
            .district(address.getDistrict())
            .street(address.getStreet())
            .detailAddress1(address.getDetailAddress1())
            .detailAddress2(address.getDetailAddress2())
            .message(address.getMessage())
            .build();
    }
}
