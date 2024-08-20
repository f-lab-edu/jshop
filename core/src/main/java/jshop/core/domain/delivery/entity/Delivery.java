package jshop.core.domain.delivery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jshop.core.domain.address.entity.Address;
import jshop.common.exception.ErrorCode;
import jshop.core.common.entity.BaseEntity;
import jshop.common.exception.JshopException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;
import org.slf4j.MDC;

@Slf4j
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

    private LocalDateTime deliveredDate;

    public void startTransit() {
        if (!deliveryState.equals(DeliveryState.PREPARING)) {
            MDC.put("error_code", String.valueOf(ErrorCode.ILLEGAL_DELIVERY_STATE.getCode()));
            log.error(ErrorCode.ILLEGAL_DELIVERY_STATE.getLogMessage(), DeliveryState.PREPARING, deliveryState);
            MDC.clear();
            throw JshopException.of(ErrorCode.ILLEGAL_DELIVERY_STATE);
        }
        deliveryState = DeliveryState.IN_TRANSIT;
    }

    public void endDelivered() {
        if (!deliveryState.equals(DeliveryState.IN_TRANSIT)) {
            MDC.put("error_code", String.valueOf(ErrorCode.ILLEGAL_DELIVERY_STATE.getCode()));
            log.error(ErrorCode.ILLEGAL_DELIVERY_STATE.getLogMessage(), DeliveryState.IN_TRANSIT, deliveryState);
            MDC.clear();
            throw JshopException.of(ErrorCode.ILLEGAL_DELIVERY_STATE);
        }
        deliveryState = DeliveryState.DELIVERED;
        deliveredDate = LocalDateTime.now();
    }

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
