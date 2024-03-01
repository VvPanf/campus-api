package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.entity.User;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks
    private UserService userService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final List<User> userList = List.of(
        new User(1l, "Ivanov Ivan", LocalDate.of(2000, 10, 10), "some@mail.ru"),
        new User(2l, "Petrov Petr", LocalDate.of(1996, 1, 2), "some-other@mail.ru"),
        new User(3l, "Alexeev Alex", LocalDate.of(1995, 12, 2), "other@mail.ru")
    );

    @Test
    public void handleGetAllUsers_UserListIsEmpty_ReturnEmptyPage() {
        // given
        when(userRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        // when
        Page<UserDto> result = userService.getAllUsers(pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetAllUsers_UserListPresent_ReturnPageWithUsers() {
        // given
        when(userRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(userList));
        // when
        Page<UserDto> result = userService.getAllUsers(pageable);
        // then
        List<UserDto> expectedUserList = List.of(
                new UserDto(1l, "Ivanov Ivan", LocalDate.of(2000, 10, 10), "some@mail.ru"),
                new UserDto(2l, "Petrov Petr", LocalDate.of(1996, 1, 2), "some-other@mail.ru"),
                new UserDto(3l, "Alexeev Alex", LocalDate.of(1995, 12, 2), "other@mail.ru")
        );
        assertArrayEquals(expectedUserList.toArray(), result.getContent().toArray());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void handleGetUsersByNameMatches_UserNotFoundInList_ReturnEmptyPage() {
        // given
        when(userRepo.findByFullNameContains(startsWith("Sid"), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        // when
        Page<UserDto> result = userService.getUsersByNameMatches("Sidorov", pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetUsersByNameMatches_UserIsInList_ReturnPageWithFondUser() {
        // given
        when(userRepo.findByFullNameContains(startsWith("Iva"), any(Pageable.class))).thenReturn(new PageImpl<>(userList.subList(0, 1)));
        // when
        Page<UserDto> result = userService.getUsersByNameMatches("Ivanov", pageable);
        // then
        UserDto expectedUser = new UserDto(1l, "Ivanov Ivan", LocalDate.of(2000, 10, 10), "some@mail.ru");
        assertEquals(1, result.getContent().size());
        assertEquals(expectedUser, result.getContent().get(0));
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void handleGetUserById_UserNotFoundInList_ReturnNull() {
        // given
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        // when
        UserDto result = userService.getUserById(1l);
        // then
        assertNull(result);
    }

    @Test
    public void handleGetUserById_UserIsInList_ReturnUserDto() {
        // given
        when(userRepo.findById(anyLong()))
                .thenReturn(Optional.of(userList.get(0)));
        // when
        UserDto result = userService.getUserById(1l);
        // then
        UserDto expectedUserDto = new UserDto(
                1l,
                "Ivanov Ivan",
                LocalDate.of(2000, 10, 10),
                "some@mail.ru");
        assertEquals(expectedUserDto, result);
    }

    @Test
    public void handleAddUser_SuccessfulAdd_ReturnNewUserDto() {
        // given
        when(userRepo.save(any())).thenReturn(userList.get(0));
        // when
        UserDto newUser = new UserDto(
                null,
                "Ivanov Ivan",
                LocalDate.of(2000, 10, 10),
                "some@mail.ru");
        UserDto result = userService.addUser(newUser);
        // then
        UserDto expectedUser = new UserDto(
                1l,
                "Ivanov Ivan",
                LocalDate.of(2000, 10, 10),
                "some@mail.ru");
        assertEquals(expectedUser, result);
    }
}