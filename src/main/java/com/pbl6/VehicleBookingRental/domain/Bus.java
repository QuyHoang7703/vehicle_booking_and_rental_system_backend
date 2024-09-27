package com.pbl6.VehicleBookingRental.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.time.Instant;

@Entity
@Table(name="bus_types")
@Getter
@Setter
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String licensePlate;
    private Instant createAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="bus_utilities", joinColumns = @JoinColumn(name = "bus_id"), inverseJoinColumns = @JoinColumn(name ="utility_id"))
    private List<Utility> utilities;

    @ManyToOne
    @JoinColumn(name = "bus_type_id")
    private BusType busType;

    @PrePersist
    public void handleBeforeCreated(){
      
        this.createAt = Instant.now();
    }
    
}
