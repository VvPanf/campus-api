package com.github.vvpanf.campusapi.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationDto {
    Long id;
    LocalDate dateOfReserv;
    LocalTime startTime;
    LocalTime endTime;
    String comment;
    Integer peopleCount;
}
