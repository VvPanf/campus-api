package com.github.vvpanf.campusapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

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
