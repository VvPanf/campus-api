package com.github.vvpanf.campusapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Campus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String address;
    @Column(name = "parking_spaces")
    Integer parkingSpaces;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "campus_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    final Set<Room> rooms = new HashSet<>();
}
