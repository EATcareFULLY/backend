package com.eatcarefully.backend.services;

import com.eatcarefully.backend.model.PreferenceName;
import com.eatcarefully.backend.repository.PreferenceNameRepository;
import com.eatcarefully.backend.service.PreferenceNamesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PreferenceNamesServiceTest {

    @Mock
    private PreferenceNameRepository preferenceNameRepository;

    @InjectMocks
    private PreferenceNamesService preferenceNamesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_CreatePreferenceName_When_NotExists() {

        String preferenceName = "test";
        when(preferenceNameRepository.findByName(preferenceName)).thenReturn(Optional.empty());

        preferenceNamesService.createPreferenceName(preferenceName);

        verify(preferenceNameRepository, times(1)).save(any(PreferenceName.class));
    }

    @Test
    void Should_ThrowException_When_PreferenceNameAlreadyExists() {

        String preferenceName = "test";
        when(preferenceNameRepository.findByName(preferenceName)).thenReturn(Optional.of(new PreferenceName()));

        assertThrows(IllegalArgumentException.class, () -> preferenceNamesService.createPreferenceName(preferenceName));
        verify(preferenceNameRepository, never()).save(any(PreferenceName.class));
    }


    @Test
    void Should_ReturnPreferenceName_When_Exists() {

        String preferenceName = "test";
        PreferenceName expectedPreferenceName = new PreferenceName();
        expectedPreferenceName.setName(preferenceName);
        when(preferenceNameRepository.findByName(preferenceName)).thenReturn(Optional.of(expectedPreferenceName));

        PreferenceName result = preferenceNamesService.getPreferenceName(preferenceName);

        assertNotNull(result);
        assertEquals(preferenceName, result.getName());
        verify(preferenceNameRepository, times(1)).findByName(preferenceName);
    }

    @Test
    void Should_ReturnNull_When_PreferenceNameDoesNotExist() {

        String preferenceName = "test";
        when(preferenceNameRepository.findByName(preferenceName)).thenReturn(Optional.empty());

        PreferenceName result = preferenceNamesService.getPreferenceName(preferenceName);

        assertNull(result);
        verify(preferenceNameRepository, times(1)).findByName(preferenceName);
    }

    @Test
    void Should_ReturnAllPreferenceNames() {

        List<PreferenceName> preferences = List.of(new PreferenceName(null, "test1", null),
                new PreferenceName(null, "test2", null));
        when(preferenceNameRepository.findAll()).thenReturn(preferences);

        List<String> result = preferenceNamesService.getAllPreferenceNames();

        assertNotNull(result);
        assertEquals(preferences.size(), result.size());


    }





}
