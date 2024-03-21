package com.github.vvpanf.campusapi.controller;

import com.github.vvpanf.campusapi.ValidationException;
import com.github.vvpanf.campusapi.dto.ReservationDto;
import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.service.ReservationService;
import com.github.vvpanf.campusapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "User management APIs")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    final String DEFAULT_PAGE = "0";
    final String DEFAULT_COUNT = "10";
    UserService userService;
    ReservationService reservationService;

    @Operation(
            summary = "Retrieve a Users by page or by matches name",
            tags = { "users", "get", "page", "nameMatches" },
            parameters = {
                    @Parameter(name = "page", description = "Number of page"),
                    @Parameter(name = "count", description = "Items count on page"),
                    @Parameter(name = "nameMatches", description = "Part of user name")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
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

    @Operation(
            summary = "Retrieve a User by Id",
            tags = { "campuses", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{user-id}")
    public ResponseEntity<?> getUser(@PathVariable("user-id") Long userId) {
        return ResponseEntity.ofNullable(userService.getUserById(userId));
    }

    @Operation(
            summary = "Add a new User",
            tags = { "users", "post" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information about new User",
                    content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json"),
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(201).body(userService.addUser(userDto));
    }

    @Operation(
            summary = "Retrieve a Reservations of User",
            tags = { "reservations", "get", "page", "count" },
            parameters = {
                    @Parameter(name = "page", description = "Number of page"),
                    @Parameter(name = "count", description = "Items count on page")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{user-id}/reservations")
    public ResponseEntity<?> getUserReservations(
            @PathVariable("user-id") Long userId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE, required = false) Integer page,
            @RequestParam(value = "count", defaultValue = DEFAULT_COUNT, required = false) Integer count
    ) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId, PageRequest.of(page, count)));
    }

    @Operation(
            summary = "Retrieve a Reservation of User by Id",
            tags = { "reservation", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{user-id}/reservations/{reservation-id}")
    public ResponseEntity<?> getUserReservation(@PathVariable("user-id") Long userId,
                                                @PathVariable("reservation-id") Long reservationId) {
        return ResponseEntity.ofNullable(reservationService.getReservationByUserIdAndId(userId, reservationId));
    }

    @Operation(
            summary = "Add a new Reservation",
            tags = { "reservations", "post" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information about new Reservation",
                    content = @Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json"),
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = ReservationDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/{user-id}/reservations")
    public ResponseEntity<?> addUserReservation(@PathVariable("user-id") Long userId, @Valid @RequestBody ReservationDto reservationDto) {
        reservationService.validateReservation(reservationDto);
        return ResponseEntity.status(201).body(reservationService.addReservation(userId, reservationDto));
    }

    @Operation(
            summary = "Add a new Room into a Reservation",
            tags = { "reservations", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema())})
    })
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
