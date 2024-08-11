package jshop.core.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jshop.core.domain.address.dto.CreateAddressRequest;
import jshop.core.domain.address.dto.UpdateAddressRequest;
import jshop.core.domain.user.entity.User;
import jshop.core.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
@Table(name = "address")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private Long id;

    /**
     * User 는 여러개의 Address를 가질 수 있음.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    public static Address of(CreateAddressRequest createAddressRequest, User user) {
        return Address
            .builder()
            .receiverName(createAddressRequest.getReceiverName())
            .receiverNumber(createAddressRequest.getReceiverNumber())
            .province(createAddressRequest.getProvince())
            .city(createAddressRequest.getCity())
            .district(createAddressRequest.getDistrict())
            .street(createAddressRequest.getStreet())
            .detailAddress1(createAddressRequest.getDetailAddress1())
            .detailAddress2(createAddressRequest.getDetailAddress2())
            .message(createAddressRequest.getMessage())
            .user(user)
            .build();
    }

    public void delete() {
        user = null;
        isDeleted = true;
    }

    public void update(UpdateAddressRequest updateAddressRequest) {
        this.receiverName = updateAddressRequest.getReceiverName();
        this.receiverNumber = updateAddressRequest.getReceiverNumber();
        this.province = updateAddressRequest.getProvince();
        this.city = updateAddressRequest.getCity();
        this.district = updateAddressRequest.getDistrict();
        this.street = updateAddressRequest.getStreet();
        this.detailAddress1 = updateAddressRequest.getDetailAddress1();
        this.detailAddress2 = updateAddressRequest.getDetailAddress2();
        this.message = updateAddressRequest.getMessage();
    }
}
