package jshop.domain.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private String message;
    private String province;
    private String city;
    private String district;
    private String street;

    @Column(name = "detail_address1")
    private String detailAddress1;

    @Column(name = "detail_address2")
    private String detailAddress2;
}
