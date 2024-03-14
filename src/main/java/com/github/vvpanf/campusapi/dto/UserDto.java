package com.github.vvpanf.campusapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotBlank(message = "User fullName is required")
    String fullName;
    @NotNull(message = "User fullName is required")
    LocalDate dateOfBirth;
    @NotBlank(message = "User email is required")
    String email;
}
