package com.github.vvpanf.campusapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Reservation dateOfReserv is required")
    LocalDate dateOfReserv;
    @NotNull(message = "Reservation startTime is required")
    LocalTime startTime;
    @NotNull(message = "Reservation endTime is required")
    LocalTime endTime;
    String comment;
    @NotNull(message = "Reservation peopleCount is required")
    Integer peopleCount;
}
