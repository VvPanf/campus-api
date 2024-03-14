package com.github.vvpanf.campusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"campus", "reservations"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String number;
    String type;
    Integer capacity;

    @ManyToOne
    Campus campus;

    @ManyToMany(mappedBy = "rooms")
    final Set<Reservation> reservations = new HashSet<>();
}
