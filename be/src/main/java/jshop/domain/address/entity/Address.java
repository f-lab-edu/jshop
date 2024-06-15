package jshop.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jshop.domain.address.dto.AddressInfoResponse;
import jshop.domain.address.dto.CreateAddressRequest;
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

    public static Address ofCreateAddressRequest(CreateAddressRequest createAddressRequest,
        User user) {
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Address{");
        sb.append("id=").append(id);
        sb.append(", receiverName='").append(receiverName).append('\'');
        sb.append(", receiverNumber='").append(receiverNumber).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", province='").append(province).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", district='").append(district).append('\'');
        sb.append(", street='").append(street).append('\'');
        sb.append(", detailAddress1='").append(detailAddress1).append('\'');
        sb.append(", detailAddress2='").append(detailAddress2).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
