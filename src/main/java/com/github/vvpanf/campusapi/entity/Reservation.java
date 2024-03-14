package com.github.vvpanf.campusapi.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"user", "rooms"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "date_of_reserv")
    LocalDate dateOfReserv;
    @Column(name = "start_time")
    LocalTime startTime;
    @Column(name = "end_time")
    LocalTime endTime;
    @Column(length = 1000)
    String comment;

    @ManyToOne
    User user;

    @ManyToMany
    @JoinTable(name = "room_in_reservation",
               joinColumns = @JoinColumn(name = "reservation_id"),
               inverseJoinColumns = @JoinColumn(name = "room_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    final List<Room> rooms = new ArrayList<>();
}
