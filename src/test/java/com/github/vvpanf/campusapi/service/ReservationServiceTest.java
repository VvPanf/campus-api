package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.ValidationException;
import com.github.vvpanf.campusapi.dto.ReservationDto;
import com.github.vvpanf.campusapi.entity.Reservation;
import com.github.vvpanf.campusapi.entity.Room;
import com.github.vvpanf.campusapi.entity.User;
import com.github.vvpanf.campusapi.repo.ReservationRepo;
import com.github.vvpanf.campusapi.repo.RoomRepo;
import com.github.vvpanf.campusapi.repo.UserRepo;
import com.github.vvpanf.campusapi.utils.TestUtils;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepo reservationRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RoomRepo roomRepo;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private ReservationService reservationService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final LocalDate dateOfReserv = LocalDate.now().plusDays(1);
    private final LocalTime startTime = LocalTime.of(10, 0);
    private final LocalTime endTime = LocalTime.of(12, 0);
    private final List<Reservation> reservationList = List.of(
        new Reservation(1l, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null),
        new Reservation(2l, dateOfReserv, LocalTime.of(11, 0), LocalTime.of(12, 0), null, null),
        new Reservation(3l, dateOfReserv, LocalTime.of(13, 0), LocalTime.of(14, 0), null, null)
    );
    private final User user = new User(1l, "User 1", LocalDate.of(2010, 10, 10), "some@mail.ru");

    @Test
    public void handleValidateReservation_NoDateOfReserv_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto();
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Не указана дата бронирования", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_NoStartTime_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto();
        reservation.setDateOfReserv(dateOfReserv);
        reservation.setEndTime(endTime);
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Не указано время начала бронирования", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_NoEndTime_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto();
        reservation.setDateOfReserv(dateOfReserv);
        reservation.setStartTime(startTime);
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Не указано время окончания бронирования", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_MessageIsTooLong_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto(
                null,
                dateOfReserv,
                startTime,
                endTime,
                "A".repeat(1001),
                null
        );
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Превышена длинна комментария 1000 символов", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_StartTimeIsAfterEndTime_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto(
                null,
                dateOfReserv,
                endTime,
                startTime,
                null,
                null
        );
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Время начала резервирования должно предшествовать времени окончания", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_StartDateIsBeforeNow_ShouldThrowException() {
        // given
        ReservationDto reservation = new ReservationDto(
                null,
                LocalDate.now().minusDays(1),
                startTime,
                endTime,
                null,
                null
        );
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservation(reservation));
        // then
        assertEquals("Дата начала и время резервирования не может быть раньше текущей даты", exception.getMessage());
    }

    @Test
    public void handleValidateReservation_AllFieldsAreOk_ShouldNotThrowException() {
        // given
        ReservationDto reservation = new ReservationDto(
                null,
                dateOfReserv,
                startTime,
                endTime,
                "Some comment",
                null
        );
        // then
        assertDoesNotThrow(() -> reservationService.validateReservation(reservation));
    }

    @Test
    public void handleGetReservationsByUserId_ReservationsNotFound_ReturnEmptyPage() {
        // given
        when(reservationRepo.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        // when
        Page<ReservationDto> result = reservationService.getReservationsByUserId(1l, pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetReservationsByUserId_ReservationsFound_ReturnPageWithReservations() {
        // given
        when(reservationRepo.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(reservationList));
        // when
        Page<ReservationDto> result = reservationService.getReservationsByUserId(1l, pageable);
        // then
        List<ReservationDto> expectedReservationList = List.of(
                new ReservationDto(1l, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null),
                new ReservationDto(2l, dateOfReserv, LocalTime.of(11, 0), LocalTime.of(12, 0), null, null),
                new ReservationDto(3l, dateOfReserv, LocalTime.of(13, 0), LocalTime.of(14, 0), null, null)
        );
        assertArrayEquals(expectedReservationList.toArray(), result.getContent().toArray());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void handleGetReservationByUserIdAndId_ReservationNotFound_ReturnNull() {
        // given
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        // when
        ReservationDto result = reservationService.getReservationByUserIdAndId(1l, 1l);
        // then
        assertNull(result);
    }

    @Test
    public void handleGetReservationByUserIdAndId_ReservationFound_ReturnReservationDto() {
        // given
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(reservationList.get(0)));
        // when
        ReservationDto result = reservationService.getReservationByUserIdAndId(1l, 1l);
        // then
        ReservationDto expectedReservation = new ReservationDto(1l, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null);
        assertEquals(expectedReservation, result);
    }

    @Test
    public void handleAddReservation_SuccessfulAdd_ReturnNewReservationDto() {
        // given
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(reservationRepo.save(any())).thenReturn(reservationList.get(0));
        // when
        ReservationDto newReservation = new ReservationDto(null, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null);
        ReservationDto result = reservationService.addReservation(1l, newReservation);
        // then
        ReservationDto expectedReservation = new ReservationDto(1l, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null);
        assertEquals(expectedReservation, result);
    }

    @Test
    public void handleAddReservation_FailedAddNoSuchUser_ReturnNull() {
        // given
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        // when
        ReservationDto newReservation = new ReservationDto(null, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null);
        ReservationDto result = reservationService.addReservation(1l, newReservation);
        // then
        assertNull(result);
    }

    @Test
    public void handleValidateReservationRoom_ReservationNotFound_ShouldThrowException() {
        // given
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservationRoom(1l, 1l, 1l));
        // then
        assertEquals("Не найдена бронь у пользователя", exception.getMessage());
    }

    @Test
    public void handleValidateReservationRoom_RoomNotFound_ShouldThrowException() {
        // given
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(new Reservation()));
        when(roomRepo.findById(anyLong())).thenReturn(Optional.empty());
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservationRoom(1l, 1l, 1l));
        // then
        assertEquals("Не найдена аудитория", exception.getMessage());
    }

    @Test
    public void handleValidateReservationRoom_ReservationContainsRoom_ShouldThrowException() {
        // given
        Room room = new Room(1l, "101", "Lab", 20, null);
        Reservation reservation = new Reservation(1l, dateOfReserv, startTime, endTime, null, null);
        reservation.getRooms().add(room);
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(reservation));
        when(roomRepo.findById(anyLong())).thenReturn(Optional.of(room));
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservationRoom(1l, 1l, 1l));
        // then
        assertEquals("Комната уже присутствует в брони", exception.getMessage());
    }

    @Test
    public void handleValidateReservationRoom_RoomReservedOnThisTime_ShouldThrowException() {
        // given
        Room room = new Room(1l, "101", "Lab", 20, null);
        Reservation reservation1 = new Reservation(1l, dateOfReserv, startTime, endTime, null, null);
        room.getReservations().add(reservation1);

        Reservation reservation2 = new Reservation(2l, dateOfReserv, startTime, endTime, null, null);
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(reservation2));
        when(roomRepo.findById(anyLong())).thenReturn(Optional.of(room));
        // when
        Exception exception = assertThrows(ValidationException.class, () -> reservationService.validateReservationRoom(1l, 1l, 1l));
        // then
        assertEquals("Комната уже забронирована на это время", exception.getMessage());
    }

    @Test
    public void handleValidateReservationRoom_RoomReservedOnOtherDay_ShouldNotThrowException() {
        // given
        Room room = new Room(1l, "101", "Lab", 20, null);
        Reservation reservation1 = new Reservation(1l, dateOfReserv, startTime, endTime, null, null);
        room.getReservations().add(reservation1);

        Reservation reservation2 = new Reservation(2l, LocalDate.now().plusDays(3), startTime, endTime, null, null);
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(reservation2));
        when(roomRepo.findById(anyLong())).thenReturn(Optional.of(room));
        // then
        assertDoesNotThrow(() -> reservationService.validateReservationRoom(1l, 1l, 1l));
    }

    @Test
    public void handleAddReservationRoom_SuccessfulAdd() {
        // given
        Reservation reservation = new Reservation(1l, dateOfReserv, startTime, endTime, null, null);
        Room room = new Room(1l, "101", "Lab", 20, null);
        when(reservationRepo.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(reservation));
        when(roomRepo.findById(anyLong())).thenReturn(Optional.of(room));
        // when
        reservationService.addReservationRoom(1l, 1l, 1l);
        // then
        verify(reservationRepo, times(1)).save(any());
        assertEquals(1, reservation.getRooms().size());
    }

    @Test
    public void handleGetReservationsByCampusIdAndRoomId_RoomNotFound_ShouldReturnEmptyPage() {
        // given
        when(roomRepo.findByIdAndCampusId(anyLong(), anyLong())).thenReturn(Optional.empty());
        // when
        Page<ReservationDto> result = reservationService.getReservationsByCampusIdAndRoomId(1l, 1l, pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetReservationsByCampusIdAndRoomId_RoomFoundNoReservation_ShouldReturnEmptyPage() {
        // given
        Room room = new Room(1l, "101", "Lab", 20, null);
        when(roomRepo.findByIdAndCampusId(anyLong(), anyLong())).thenReturn(Optional.of(room));
        // when
        Page<ReservationDto> result = reservationService.getReservationsByCampusIdAndRoomId(1l, 1l, pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetReservationsByCampusIdAndRoomId_RoomFoundAndReservationFound_ShouldReturnPageWithReservation() {
        // given
        Room room = new Room(1l, "101", "Lab", 20, null);
        room.getReservations().addAll(reservationList);
        when(roomRepo.findByIdAndCampusId(anyLong(), anyLong())).thenReturn(Optional.of(room));
        // when
        Page<ReservationDto> result = reservationService.getReservationsByCampusIdAndRoomId(1l, 1l, pageable);
        // then
        List<ReservationDto> expectedReservationList = List.of(
            new ReservationDto(3l, dateOfReserv, LocalTime.of(13, 0), LocalTime.of(14, 0), null, null),
            new ReservationDto(2l, dateOfReserv, LocalTime.of(11, 0), LocalTime.of(12, 0), null, null),
            new ReservationDto(1l, dateOfReserv, LocalTime.of(10, 0), LocalTime.of(11, 0), null, null)
        );
        assertArrayEquals(expectedReservationList.toArray(), result.getContent().toArray());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalElements());
    }
}
