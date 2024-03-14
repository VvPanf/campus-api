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
public class CampusDto {
    Long id;
    @NotBlank(message = "Campus name is required")
    String name;
    @NotBlank(message = "Campus address is required")
    String address;
    @NotNull(message = "Campus parkingSpaces is required")
    Integer parkingSpaces;
}
