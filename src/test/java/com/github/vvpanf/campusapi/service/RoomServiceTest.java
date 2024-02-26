package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.RoomDto;
import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.entity.Campus;
import com.github.vvpanf.campusapi.entity.Reservation;
import com.github.vvpanf.campusapi.entity.Room;
import com.github.vvpanf.campusapi.entity.User;
import com.github.vvpanf.campusapi.repo.CampusRepo;
import com.github.vvpanf.campusapi.repo.RoomRepo;
import com.github.vvpanf.campusapi.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepo roomRepo;
    @Mock
    private CampusRepo campusRepo;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks
    private RoomService roomService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final LocalDate reservationDate = LocalDate.now().plusDays(1);
    private final List<Room> roomList = List.of(
        new Room(1l, "101", "Lab", 20, null),
        new Room(2l, "201", "Lecture", 100, null),
        new Room(3l, "301", "Lab", 15, null)
    );
    private final List<Reservation> reservationList = List.of(
        new Reservation(1l, reservationDate, LocalTime.of(12, 0), LocalTime.of(13, 0), null, null),
        new Reservation(2l, reservationDate, LocalTime.of(13, 0), LocalTime.of(14, 0), null, null),
        new Reservation(3l, reservationDate, LocalTime.of(15, 0), LocalTime.of(16, 0), null, null)
    );

    @BeforeEach
    public void beforeAll() {
        roomList.get(0).getReservations().clear();
        roomList.get(0).getReservations().addAll(reservationList);
    }

    @Test
    public void handleGetRoomsByCampusId_WrongCampusId_ReturnEmptyPage() {
        // given
        when(roomRepo.findAllByCampusId(eq(1l), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        // when
        Page<RoomDto> result = roomService.getRoomsByCampusId(1l, pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetRoomsByCampusId_RightCampusId_ReturnPageWithRooms() {
        // given
        when(roomRepo.findAllByCampusId(eq(1l), any(Pageable.class))).thenReturn(new PageImpl<>(roomList));
        // when
        Page<RoomDto> result = roomService.getRoomsByCampusId(1l, pageable);
        // then
        List<RoomDto> expectedRoomList = List.of(
                new RoomDto(1l, "101", "Lab", 20),
                new RoomDto(2l, "201", "Lecture", 100),
                new RoomDto(3l, "301", "Lab", 15)
        );
        assertArrayEquals(expectedRoomList.toArray(), result.getContent().toArray());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void handleGetRoomFiltered_SearchByNumberOfSeats_ReturnPageWithRooms() {
        when(roomRepo.findAllByCampusId(eq(1l))).thenReturn(roomList.subList(0, 1));
        // found
        getRoomFilteredTestWithParams(LocalTime.of(11, 30), null, true);
        getRoomFilteredTestWithParams(null, LocalTime.of(11, 30), true);
        getRoomFilteredTestWithParams(LocalTime.of(14, 0), LocalTime.of(15, 0), true);
        getRoomFilteredTestWithParams(LocalTime.of(10, 0), LocalTime.of(11, 0), true);
        getRoomFilteredTestWithParams(LocalTime.of(21, 0), LocalTime.of(22, 0), true);
        // not found
        getRoomFilteredTestWithParams(LocalTime.of(13, 0), LocalTime.of(14, 0), false);
        getRoomFilteredTestWithParams(LocalTime.of(10, 0), LocalTime.of(19, 0), false);
        getRoomFilteredTestWithParams(LocalTime.of(10, 0), LocalTime.of(19, 0), false);
        getRoomFilteredTestWithParams(LocalTime.of(11, 0), LocalTime.of(16, 0), false);
    }

    private void getRoomFilteredTestWithParams(LocalTime start, LocalTime end, boolean isFound) {
        // given
        RoomService.RoomSearchParams searchParams = new RoomService.RoomSearchParams(reservationDate, start, end, null);
        // when
        Page<RoomDto> result = roomService.getRoomFiltered(1l, pageable, searchParams);
        // then
        List<RoomDto> expectedRoomList = List.of(
                new RoomDto(1l, "101", "Lab", 20)
        );
        if (isFound) {
            assertArrayEquals(expectedRoomList.toArray(), result.getContent().toArray());
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
        } else {
            assertEquals(0, result.getContent().size());
            assertEquals(0, result.getTotalElements());
            assertEquals(0, result.getTotalPages());
        }
    }

    @Test
    public void handleGetUserById_UserNotFoundInList_ReturnNull() {
        // given
        when(roomRepo.findByIdAndCampusId(anyLong(), anyLong())).thenReturn(Optional.empty());
        // when
        RoomDto result = roomService.getRoomByIdAndCampusId(1l, 0l);
        // then
        assertNull(result);
    }

    @Test
    public void handleGetUserById_UserIsInList_ReturnUserDto() {
        // given
        when(roomRepo.findByIdAndCampusId(anyLong(), anyLong())).thenReturn(Optional.of(new Room(1l, "101", "Lab", 20, null)));
        // when
        RoomDto result = roomService.getRoomByIdAndCampusId(1l, 1l);
        // then
        RoomDto expectedRoomDto = new RoomDto(1l, "101", "Lab", 20);
        assertEquals(expectedRoomDto, result);
    }

    @Test
    public void handleAddRoom_SuccessfulAdd_ReturnNewRoomDto() {
        // given
        when(campusRepo.findById(anyLong())).thenReturn(Optional.of(new Campus()));
        when(roomRepo.save(any())).thenReturn(roomList.get(0));
        // when
        RoomDto newRoom = new RoomDto(null, "101", "Lab", 20);
        RoomDto result = roomService.addRoom(1l, newRoom);
        // then
        RoomDto expectedRoom = new RoomDto(1l, "101", "Lab", 20);
        assertEquals(expectedRoom, result);
    }

    @Test
    public void handleAddRoom_FailedAddNoSuchCampus_ReturnNull() {
        // given
        when(campusRepo.findById(anyLong())).thenReturn(Optional.empty());
        // when
        RoomDto newRoom = new RoomDto(null, "101", "Lab", 20);
        RoomDto result = roomService.addRoom(1l, newRoom);
        // then
        assertNull(result);
    }
}