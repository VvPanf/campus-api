package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.ValidationException;
import com.github.vvpanf.campusapi.dto.ReservationDto;
import com.github.vvpanf.campusapi.entity.Reservation;
import com.github.vvpanf.campusapi.entity.Room;
import com.github.vvpanf.campusapi.repo.ReservationRepo;
import com.github.vvpanf.campusapi.repo.RoomRepo;
import com.github.vvpanf.campusapi.repo.UserRepo;
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
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationService {
    ReservationRepo reservationRepo;
    UserRepo userRepo;
    RoomRepo roomRepo;
    ModelMapper modelMapper;

    public Page<ReservationDto> getReservationsByUserId(Long userId, Pageable pageable) {
        return reservationRepo.findAllByUserId(userId, pageable).map(reservation -> modelMapper.map(reservation, ReservationDto.class));
    }

    public ReservationDto getReservationByUserIdAndId(Long userId, Long reservationId) {
        return reservationRepo.findByIdAndUserId(reservationId, userId).map(reservation -> modelMapper.map(reservation, ReservationDto.class)).orElse(null);
    }

    public void validateReservation(ReservationDto res) throws ValidationException {
        if (res.getDateOfReserv() == null) throw new ValidationException("Не указана дата бронирования");
        if (res.getStartTime() == null) throw new ValidationException("Не указано время начала бронирования");
        if (res.getEndTime() == null) throw new ValidationException("Не указано время окончания бронирования");
        if (res.getComment() != null && res.getComment().length() > 1000) throw new ValidationException("Превышена длинна комментария 1000 символов");
        if (!res.getStartTime().isBefore(res.getEndTime())) throw new ValidationException("Время начала резервирования должно предшествовать времени окончания");
        if (res.getDateOfReserv().isBefore(LocalDate.now())) throw new ValidationException("Дата начала и время резервирования не может быть раньше текущей даты");
    }

    public ReservationDto addReservation(Long userId, ReservationDto reservationDto) {
        return userRepo.findById(userId).map(user -> {
            Reservation reservation = reservationRepo.save(modelMapper.map(reservationDto, Reservation.class));
            user.getReservations().add(reservation);
            return modelMapper.map(reservation, ReservationDto.class);
        }).orElse(null);
    }

    public void validateReservationRoom(Long userId, Long reservationId, Long roomId) {
        Reservation reservation = reservationRepo.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ValidationException("Не найдена бронь у пользователя"));
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ValidationException("Не найдена аудитория"));
        if (reservation.getRooms().contains(room)) throw new ValidationException("Комната уже присутствует в брони");
        if (room.getReservations().stream()
                .filter(res -> reservation.getDateOfReserv().equals(res.getDateOfReserv()))
                .anyMatch(res -> !(
                    (reservation.getStartTime().isAfter(res.getStartTime())
                            &&  reservation.getEndTime().isAfter(res.getEndTime()))
                     || (reservation.getStartTime().isBefore(res.getStartTime())
                            &&  reservation.getEndTime().isBefore(res.getEndTime()))
        ))) throw new ValidationException("Комната уже забронирована на это время");
    }

    public void addReservationRoom(Long userId, Long reservationId, Long roomId) {
        Reservation reservation = reservationRepo.findByIdAndUserId(reservationId, userId).get();
        Room room = roomRepo.findById(roomId).get();
        reservation.getRooms().add(room);
        reservationRepo.save(reservation);
    }

    public Page<ReservationDto> getReservationsByCampusIdAndRoomId(Long campusId, Long roomId, Pageable pageable) {
        return roomRepo.findByIdAndCampusId(roomId, campusId)
            .map(room -> {
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), room.getReservations().size());
                List<ReservationDto> result = room.getReservations().stream()
                        .skip(start)
                        .limit(end - start)
                        .map(res -> modelMapper.map(res, ReservationDto.class))
                        .toList();
                return new PageImpl<>(result);
            }).orElse(new PageImpl<>(Collections.emptyList()));
    }
}
