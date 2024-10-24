package com.pbl6.VehicleBookingRental.user.domain.bookingcar;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.LicenseTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "driver")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String citizenID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfIssue;
    private String placeOfIssue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate expiryDate;
    private String licensePlateNumber;
    private String driverLicenseNumber;
    private LicenseTypeEnum licenseType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate issueDateLicense;
    private String permanentAddress;
    private String phoneNumberOfRelative;
    private String location;
    private double ratingTotal;
    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum approvalStatus;

    @OneToMany(mappedBy = "driver",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @PrePersist
    public void initialRatingTotal() {
        this.ratingTotal = 0.0;
        this.approvalStatus = ApprovalStatusEnum.PENDING_APPROVAL;
    }

}
