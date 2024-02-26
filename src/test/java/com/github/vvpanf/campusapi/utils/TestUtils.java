package com.github.vvpanf.campusapi.utils;

import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {
    public static <T> void checkEmptyPage(Page<T> result) {
        assertTrue(result.getContent().isEmpty());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getTotalElements());
    }
}
