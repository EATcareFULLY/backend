package com.eatcarefully.backend.services;

import com.eatcarefully.backend.repository.TagRepository;
import com.eatcarefully.backend.service.TagService;
import com.eatcarefully.backend.model.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import com.eatcarefully.backend.model.Tag;
import com.eatcarefully.backend.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TagServiceTest {


    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_ReturnExistingTag_When_Found() {

        String tagName = "existingTag";
        Tag existingTag = new Tag();
        existingTag.setName(tagName);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(existingTag));

        Tag result = tagService.findOrCreateTag(tagName);

        assertNotNull(result);
        assertEquals(tagName, result.getName());
        verify(tagRepository, times(1)).findByName(tagName);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void Should_CreateNewTag_When_NotFound() {

        String tagName = "newTag";
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Tag()));

        Tag result = tagService.findOrCreateTag(tagName);


        assertNotNull(result);
        verify(tagRepository, times(2)).findByName(tagName);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }



}
