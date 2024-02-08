package com.github.vvpanf.campusapi.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {
    Long id;
    String number;
    String type;
    Integer capacity;
}
