package com.pbl6.VehicleBookingRental.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_partner")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BusinessPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
//    private String businessLicense;
    private String businessName;
    private String emailOfRepresentative;
    private String nameOfRepresentative;
    private String phoneOfRepresentative;
    private String address;
//    @Enumerated(EnumType.STRING)
    private PartnerTypeEnum partnerType;
//    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum approvalStatus;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String avatar;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String policy;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @OneToOne(mappedBy = "businessPartner",cascade = CascadeType.ALL)
    @JsonIgnore
    private CarRentalPartner carRentalPartner;

    @OneToOne(mappedBy = "businessPartner",cascade = CascadeType.ALL)
    @JsonIgnore
    private BusPartner busPartner;


}
