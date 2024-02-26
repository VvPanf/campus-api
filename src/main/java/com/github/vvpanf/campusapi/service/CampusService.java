package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.CampusDto;
import com.github.vvpanf.campusapi.entity.Campus;
import com.github.vvpanf.campusapi.repo.CampusRepo;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampusService {
    CampusRepo campusRepo;
    ModelMapper modelMapper;

    public Page<CampusDto> getAllCampuses(Pageable pageable) {
        return campusRepo.findAll(pageable).map(campus -> modelMapper.map(campus, CampusDto.class));
    }

    public CampusDto getCampusById(Long id) {
        return campusRepo.findById(id).map(campus -> modelMapper.map(campus, CampusDto.class)).orElse(null);
    }

    public CampusDto getCampusByName(String name) {
        return campusRepo.findByName(name).map(campus -> modelMapper.map(campus, CampusDto.class)).orElse(null);
    }

    public CampusDto addCampus(CampusDto campusDto) {
        Campus newCampus = campusRepo.save(modelMapper.map(campusDto, Campus.class));
        return modelMapper.map(newCampus, CampusDto.class);
    }
}
