package com.pbl6.VehicleBookingRental.user.domain.bus_service;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.time.Instant;

@Entity
@Table(name="buses")
@Getter
@Setter
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String licensePlate;
    private Instant createAt;
    private Instant updateAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="bus_utilities", joinColumns = @JoinColumn(name = "bus_id"), inverseJoinColumns = @JoinColumn(name ="utility_id"))
    @JsonIgnore
    private List<Utility> utilities;

    @ManyToOne
    @JoinColumn(name = "bus_type_id")
    private BusType busType;

    @ManyToOne
    @JoinColumn(name="bus_partner_id")
    @JsonIgnore
    private BusPartner busPartner;

    @PrePersist
    public void handleBeforeCreated(){
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdated(){
        this.updateAt = Instant.now();
    }

}
