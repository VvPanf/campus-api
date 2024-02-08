package com.github.vvpanf.campusapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampusDto {
    Long id;
    String name;
    String address;
    Integer parkingSpaces;
    Integer roomCount;
}
