package com.github.vvpanf.campusapi.service;

import com.github.vvpanf.campusapi.dto.CampusDto;
import com.github.vvpanf.campusapi.dto.UserDto;
import com.github.vvpanf.campusapi.entity.Campus;
import com.github.vvpanf.campusapi.entity.User;
import com.github.vvpanf.campusapi.repo.CampusRepo;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampusServiceTest {
    @Mock
    private CampusRepo campusRepo;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks
    private CampusService campusService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final List<Campus> campusList = List.of(
        new Campus(1l, "Campus 1", "Some address", 10),
        new Campus(2l, "Campus 2", "Some other address", 15),
        new Campus(3l, "Campus 3", "Address", 30)
    );

    @Test
    public void handleGetAllCampuses_CampusListIsEmpty_ReturnEmptyPage() {
        // given
        when(campusRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        // when
        Page<CampusDto> result = campusService.getAllCampuses(pageable);
        // then
        TestUtils.checkEmptyPage(result);
    }

    @Test
    public void handleGetAllCampuses_CampusListPresent_ReturnPageWithCampuses() {
        // given
        when(campusRepo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(campusList));
        // when
        Page<CampusDto> result = campusService.getAllCampuses(pageable);
        // then
        List<CampusDto> expectedCampusList = List.of(
                new CampusDto(1l, "Campus 1", "Some address", 10),
                new CampusDto(2l, "Campus 2", "Some other address", 15),
                new CampusDto(3l, "Campus 3", "Address", 30)
        );
        assertArrayEquals(expectedCampusList.toArray(), result.getContent().toArray());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getTotalElements());
    }

    @Test
    public void handleGetCampusById_CampusNotFoundInList_ReturnNull() {
        // given
        when(campusRepo.findById(eq(1l))).thenReturn(Optional.empty());
        // when
        CampusDto result = campusService.getCampusById(1l);
        // then
        assertNull(result);
    }

    @Test
    public void handleGetCampusById_CampusIsInList_ReturnCampusDto() {
        // given
        when(campusRepo.findById(eq(1l))).thenReturn(Optional.of(new Campus(1l, "Campus 1", "Some address", 10)));
        // when
        CampusDto result = campusService.getCampusById(1l);
        // then
        CampusDto campusDto = new CampusDto(1l, "Campus 1", "Some address", 10);
        assertEquals(campusDto, result);
    }

    @Test
    public void handleGetCampusByName_CampusNotFoundInList_ReturnNull() {
        // given
        when(campusRepo.findByName(eq("Campus 1"))).thenReturn(Optional.empty());
        // when
        CampusDto result = campusService.getCampusByName("Campus 1");
        // then
        assertNull(result);
    }

    @Test
    public void handleGetCampusByName_CampusIsInList_ReturnCampusDto() {
        // given
        when(campusRepo.findByName(eq("Campus 1"))).thenReturn(Optional.of(new Campus(1l, "Campus 1", "Some address", 10)));
        // when
        CampusDto result = campusService.getCampusByName("Campus 1");
        // then
        CampusDto campusDto = new CampusDto(1l, "Campus 1", "Some address", 10);
        assertEquals(campusDto, result);
    }

    @Test
    public void handleAddCampus_SuccessfulAdd_ReturnNewCampusDto() {
        // given
        when(campusRepo.save(any())).thenReturn(new Campus(1l, "Campus 1", "Some address", 10));
        // when
        CampusDto newCampus = new CampusDto(null, "Campus 1", "Some address", 10);
        CampusDto result = campusService.addCampus(newCampus);
        // then
        CampusDto expectedCampus = new CampusDto(1l, "Campus 1", "Some address", 10);
        assertEquals(expectedCampus, result);
    }


}