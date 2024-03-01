package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.entity.User;
import com.github.vvpanf.campusapi.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepo userRepo;
    ModelMapper modelMapper;

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(user -> modelMapper.map(user, UserDto.class));
    }

    public Page<UserDto> getUsersByNameMatches(String nameMatches, Pageable pageable) {
        return userRepo.findByFullNameContains(nameMatches, pageable).map(user -> modelMapper.map(user, UserDto.class));
    }

    public UserDto getUserById(Long id) {
        return userRepo.findById(id).map(user -> modelMapper.map(user, UserDto.class)).orElse(null);
    }

    public UserDto addUser(UserDto userDto) {
        User user = userRepo.save(modelMapper.map(userDto, User.class));
        return modelMapper.map(user, UserDto.class);
    }
}
