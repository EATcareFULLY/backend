package com.eatcarefully.backend.service;

import com.eatcarefully.backend.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import com.eatcarefully.backend.model.Tag;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;


    @Cacheable(cacheNames = "tags", key="#name")
    public Tag findOrCreateTag(String name){

        Optional<Tag> dbTag = tagRepository.findByName(name);

        if(! dbTag.isPresent()){
            Tag newTag = new Tag();
            newTag.setName(name);
            tagRepository.save(newTag);
            dbTag = tagRepository.findByName(name);
        }

        return dbTag.get();


    }
}
