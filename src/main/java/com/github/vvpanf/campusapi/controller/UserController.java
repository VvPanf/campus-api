package com.github.vvpanf.campusapi.controller;

import com.github.vvpanf.campusapi.ValidationException;
import com.github.vvpanf.campusapi.dto.ReservationDto;
import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.service.ReservationService;
import com.github.vvpanf.campusapi.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    final String DEFAULT_PAGE = "0";
    final String DEFAULT_COUNT = "10";
    UserService userService;
    ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count,
            @RequestParam(value = "nameMatches", required = false) String nameMatches
    ) {
        if (nameMatches != null) {
            return ResponseEntity.ok(userService.getUsersByNameMatches(nameMatches, PageRequest.of(page, count)));
        }
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, count)));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<?> getUser(@PathVariable("user-id") Long userId) {
        return ResponseEntity.ofNullable(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(201).body(userService.addUser(userDto));
    }

    @GetMapping("/{user-id}/reservations")
    public ResponseEntity<?> getUserReservations(
            @PathVariable("user-id") Long userId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count
    ) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId, PageRequest.of(page, count)));
    }

    @GetMapping("/{user-id}/reservations/{reservation-id}")
    public ResponseEntity<?> getUserReservation(@PathVariable("user-id") Long userId,
                                                @PathVariable("reservation-id") Long reservationId) {
        return ResponseEntity.ofNullable(reservationService.getReservationByUserIdAndId(userId, reservationId));
    }

    @PostMapping("/{user-id}/reservations")
    public ResponseEntity<?> addUserReservation(@PathVariable("user-id") Long userId, @RequestBody ReservationDto reservationDto) {
        reservationService.validateReservation(reservationDto);
        return ResponseEntity.status(201).body(reservationService.addReservation(userId, reservationDto));
    }

    @PutMapping("/{user-id}/reservations/{reservation-id}/rooms/{room-id}")
    public ResponseEntity<?> addReservationRoom(@PathVariable("user-id") Long userId,
                                                @PathVariable("reservation-id") Long reservationId,
                                                @PathVariable("room-id") Long roomId) {
        reservationService.validateReservationRoom(userId, reservationId, roomId);
        reservationService.addReservationRoom(userId, reservationId, roomId);
        return ResponseEntity.status(201).build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleException(ValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
