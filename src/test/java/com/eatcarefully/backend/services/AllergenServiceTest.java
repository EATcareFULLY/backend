package com.eatcarefully.backend.services;

import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Tag;
import com.eatcarefully.backend.repository.AllergenRepository;
import com.eatcarefully.backend.service.AllergenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class AllergenServiceTest {


    @Mock
    private AllergenRepository allergenRepository;

    @InjectMocks
    private AllergenService allergenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_ReturnExistingTag_When_Found() {

        String name = "existingAllergen";
        Allergen allergen = new Allergen();
        allergen.setName(name);
        when(allergenRepository.findByName(name)).thenReturn(Optional.of(allergen));

        Allergen result = allergenService.findOrCreateAllergen(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(allergenRepository, times(1)).findByName(name);
        verify(allergenRepository, never()).save(any(Allergen.class));
    }

    @Test
    void Should_CreateNewTag_When_NotFound() {

        String allergen = "newAllergen";
        when(allergenRepository.findByName(allergen)).thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Allergen()));

        Allergen result = allergenService.findOrCreateAllergen(allergen);


        assertNotNull(result);
        verify(allergenRepository, times(2)).findByName(allergen);
        verify(allergenRepository, times(1)).save(any(Allergen.class));
    }



}
