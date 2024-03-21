package com.github.vvpanf.campusapi.controller;

import com.github.vvpanf.campusapi.dto.CampusDto;
import com.github.vvpanf.campusapi.dto.RoomDto;
import com.github.vvpanf.campusapi.service.CampusService;
import com.github.vvpanf.campusapi.service.ReservationService;
import com.github.vvpanf.campusapi.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Tag(name = "Campus", description = "Campus management APIs")
@RestController
@RequestMapping("/campuses")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampusController {
    final String DEFAULT_PAGE = "0";
    final String DEFAULT_COUNT = "10";
    CampusService campusService;
    RoomService roomService;
    ReservationService reservationService;

    @Operation(
            summary = "Retrieve a Campuses by page or by name",
            tags = { "campuses", "get", "page", "name" },
            parameters = {
                    @Parameter(name = "page", description = "Number of page"),
                    @Parameter(name = "count", description = "Items count on page"),
                    @Parameter(name = "name", description = "Name of campus")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
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

    @Operation(
            summary = "Retrieve a Campus by Id",
            tags = { "campuses", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CampusDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{campus-id}")
    public ResponseEntity<?> getCampus(@PathVariable("campus-id") Long campusId) {
        return ResponseEntity.ofNullable(campusService.getCampusById(campusId));
    }

    @Operation(
            summary = "Add a new Campus",
            tags = { "campuses", "post" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information about new Campus",
                    content = @Content(schema = @Schema(implementation = CampusDto.class), mediaType = "application/json"),
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = CampusDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping
    public ResponseEntity<?> createCampus(@Valid @RequestBody CampusDto campusDto) {
        return ResponseEntity.status(201).body(campusService.addCampus(campusDto));
    }

    @Operation(
            summary = "Retrieve a Rooms of Campus by parameters",
            tags = { "rooms", "get", "page", "count", "reservationDate", "available", "number of seats" },
            parameters = {
                    @Parameter(name = "page", description = "Number of page"),
                    @Parameter(name = "count", description = "Items count on page"),
                    @Parameter(name = "reservationDate", description = "Date of room reservation"),
                    @Parameter(name = "availableFrom", description = "Time of reservation room available from"),
                    @Parameter(name = "availableUntil", description = "Time of reservation room available until"),
                    @Parameter(name = "minNumberOfSeats", description = "Minimal number of seats in the room")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
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

    @Operation(
            summary = "Retrieve a Room in Campus by Id",
            tags = { "rooms", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = RoomDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{campus-id}/rooms/{room-id}")
    public ResponseEntity<?> getCampusClassroom(
            @PathVariable("campus-id") Long campusId,
            @PathVariable("room-id") Long roomId
    ) {
        return ResponseEntity.ofNullable(roomService.getRoomByIdAndCampusId(campusId, roomId));
    }

    @Operation(
            summary = "Add a new Room",
            tags = { "rooms", "post" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Information about new Room",
                    content = @Content(schema = @Schema(implementation = RoomDto.class), mediaType = "application/json"),
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = RoomDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/{campus-id}/rooms")
    public ResponseEntity<?> createCampusRoom(
            @PathVariable("campus-id") Long campusId,
            @Valid @RequestBody RoomDto roomDto
    ) {
        return ResponseEntity.status(201).body(roomService.addRoom(campusId, roomDto));
    }

    @Operation(
            summary = "Retrieve a Reservations of a Room in Campus by Id",
            tags = { "reservations", "rooms", "get", "page" },
            parameters = {
                    @Parameter(name = "page", description = "Number of page"),
                    @Parameter(name = "count", description = "Items count on page")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
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
