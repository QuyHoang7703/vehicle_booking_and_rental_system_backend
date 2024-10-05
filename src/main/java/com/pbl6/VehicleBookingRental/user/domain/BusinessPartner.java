package com.pbl6.VehicleBookingRental.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_partner")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusinessPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String business_license;
    private String business_name;
    private String email_of_representative;
    private String nam_of_representative;
    private String phone_of_representative;
    private String address;
    private String partner_type;

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
