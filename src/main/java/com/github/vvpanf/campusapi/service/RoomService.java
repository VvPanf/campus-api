package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.RoomDto;
import com.github.vvpanf.campusapi.entity.Campus;
import com.github.vvpanf.campusapi.entity.Room;
import com.github.vvpanf.campusapi.repo.CampusRepo;
import com.github.vvpanf.campusapi.repo.RoomRepo;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomService {
    RoomRepo roomRepo;
    CampusRepo campusRepo;
    ModelMapper modelMapper;

    public record RoomSearchParams(LocalDate reservationDate, LocalTime availableFrom, LocalTime availableUntil, Integer minNumberOfSeats) {}

    public Page<RoomDto> getRoomsByCampusId(Long campusId, Pageable pageable) {
        return roomRepo.findAllByCampusId(campusId, pageable).map(room -> modelMapper.map(room, RoomDto.class));
    }

    public Page<RoomDto> getRoomFiltered(Long campusId, Pageable pageable, RoomSearchParams searchParams) {
        List<RoomDto> result = roomRepo.findAllByCampusId(campusId).stream()
                .filter(room -> filterByMinNumberOfSeats(searchParams, room))
                .filter(room -> filterByAvailableFrom(searchParams, room))
                .filter(room -> filterByAvailableUnlit(searchParams, room))
                .filter(room -> filterByAvailableFromAndAvailableUnlit(searchParams, room))
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        return new PageImpl<>(result.subList(start, end), pageable, result.size());
    }

    public RoomDto getRoomByIdAndCampusId(Long campusId, Long roomId) {
        return roomRepo.findByIdAndCampusId(roomId, campusId).map(room -> modelMapper.map(room, RoomDto.class)).orElse(null);
    }

    public RoomDto addRoom(Long campusId, RoomDto roomDto) {
        return campusRepo.findById(campusId).map(campus -> {
            Room newRoom = roomRepo.save(modelMapper.map(roomDto, Room.class));
            campus.getRooms().add(newRoom);
            return modelMapper.map(newRoom, RoomDto.class);
        }).orElse(null);
    }

    private boolean filterByMinNumberOfSeats(RoomSearchParams searchParams, Room room) {
        return searchParams.minNumberOfSeats() == null || room.getCapacity() >= searchParams.minNumberOfSeats();
    }

    private boolean filterByAvailableFrom(RoomSearchParams searchParams, Room room) {
        return searchParams.availableFrom() == null || room.getReservations().stream()
                .noneMatch(res -> (searchParams.reservationDate() == null || res.getDateOfReserv().isEqual(searchParams.reservationDate()))
                        && dateStartBetween(searchParams.availableFrom(), res.getStartTime(), res.getEndTime()));
    }

    private boolean filterByAvailableUnlit(RoomSearchParams searchParams, Room room) {
        return searchParams.availableUntil() == null || room.getReservations().stream()
                .noneMatch(res -> (searchParams.reservationDate() == null || res.getDateOfReserv().isEqual(searchParams.reservationDate()))
                        && dateEndBetween(searchParams.availableUntil(), res.getStartTime(), res.getEndTime()));
    }

    private boolean filterByAvailableFromAndAvailableUnlit(RoomSearchParams searchParams, Room room) {
        return searchParams.availableFrom() == null || searchParams.availableUntil() == null
                || room.getReservations().stream()
                .allMatch(res -> (searchParams.reservationDate() == null || !res.getDateOfReserv().isEqual(searchParams.reservationDate()))
                        || (searchParams.availableFrom().isAfter(res.getStartTime()) && searchParams.availableUntil().isAfter(res.getEndTime()))
                        || (searchParams.availableFrom().isBefore(res.getStartTime()) && searchParams.availableUntil().isBefore(res.getEndTime())));
    }

    private boolean dateStartBetween(LocalTime date, LocalTime start, LocalTime end) {
        return !date.isBefore(start) && date.isBefore(end);
    }

    private boolean dateEndBetween(LocalTime date, LocalTime start, LocalTime end) {
        return date.isAfter(start) && !date.isAfter(end);
    }
}
