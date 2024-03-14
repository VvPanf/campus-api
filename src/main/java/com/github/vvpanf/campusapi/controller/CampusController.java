package com.github.vvpanf.campusapi.controller;

import com.github.vvpanf.campusapi.dto.CampusDto;
import com.github.vvpanf.campusapi.dto.RoomDto;
import com.github.vvpanf.campusapi.service.CampusService;
import com.github.vvpanf.campusapi.service.ReservationService;
import com.github.vvpanf.campusapi.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/campuses")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampusController {
    final String DEFAULT_PAGE = "0";
    final String DEFAULT_COUNT = "10";
    CampusService campusService;
    RoomService roomService;
    ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getCampuses(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count,
            @RequestParam(value = "name", required = false) String name
    ) {
        if (name != null) {
            return ResponseEntity.ofNullable(campusService.getCampusByName(name));
        }
        return ResponseEntity.ok(campusService.getAllCampuses(PageRequest.of(page, count)));
    }

    @GetMapping("/{campus-id}")
    public ResponseEntity<?> getCampus(@PathVariable("campus-id") Long campusId) {
        return ResponseEntity.ofNullable(campusService.getCampusById(campusId));
    }

    @PostMapping
    public ResponseEntity<?> createCampus(@Valid @RequestBody CampusDto campusDto) {
        return ResponseEntity.status(201).body(campusService.addCampus(campusDto));
    }

    @GetMapping("/{campus-id}/rooms")
    public ResponseEntity<?> getCampusClassroom(
            @PathVariable("campus-id") Long campusId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count,
            @RequestParam(value = "reservationDate", required = false) LocalDate reservationDate,
            @RequestParam(value = "availableFrom", required = false) LocalTime availableFrom,
            @RequestParam(value = "availableUntil", required = false) LocalTime availableUntil,
            @RequestParam(value = "minNumberOfSeats", required = false) Integer minNumberOfSeats
            ) {
        if (reservationDate == null && availableFrom == null && availableUntil == null && minNumberOfSeats == null) {
            return ResponseEntity.ok(roomService.getRoomsByCampusId(campusId, PageRequest.of(page, count)));
        }
        return ResponseEntity.ok(roomService.getRoomsFiltered(campusId, PageRequest.of(page, count),
                new RoomService.RoomSearchParams(reservationDate, availableFrom, availableUntil, minNumberOfSeats)));
    }

    @GetMapping("/{campus-id}/rooms/{room-id}")
    public ResponseEntity<?> getCampusClassroom(
            @PathVariable("campus-id") Long campusId,
            @PathVariable("room-id") Long roomId
    ) {
        return ResponseEntity.ofNullable(roomService.getRoomByIdAndCampusId(campusId, roomId));
    }

    @PostMapping("/{campus-id}/rooms")
    public ResponseEntity<?> createCampusRoom(
            @PathVariable("campus-id") Long campusId,
            @Valid @RequestBody RoomDto roomDto
    ) {
        return ResponseEntity.status(201).body(roomService.addRoom(campusId, roomDto));
    }

    @GetMapping("/{campus-id}/rooms/{room-id}/reservations")
    public ResponseEntity<?> getCampusRoomReservations(
            @PathVariable("campus-id") Long campusId,
            @PathVariable("room-id") Long roomId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count
    ) {
        return ResponseEntity.ok(reservationService.getReservationsByCampusIdAndRoomId(campusId, roomId, PageRequest.of(page, count)));
    }
}
