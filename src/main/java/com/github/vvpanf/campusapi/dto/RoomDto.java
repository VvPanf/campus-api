package com.github.vvpanf.campusapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {
    Long id;
    @NotBlank(message = "Room number is required")
    String number;
    @NotBlank(message = "Room capacity is required")
    String type;
    @NotNull(message = "Room capacity is required")
    Integer capacity;
}
