package com.simplified.interconnected.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "experts")
@Data
public class ExpertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String otherDetails;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "expert_service", joinColumns = @JoinColumn(name = "expert_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id", referencedColumnName = "id"))
    private List<ServiceEntity> services = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "expert_timeslot", joinColumns = @JoinColumn(name = "expert_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "timeslot_id", referencedColumnName = "id"))
    private List<TimeSlotEntity> timeslots = new ArrayList<>();
}

