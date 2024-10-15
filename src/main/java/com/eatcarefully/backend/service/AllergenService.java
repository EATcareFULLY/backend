package com.eatcarefully.backend.service;


import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.repository.AllergenRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AllergenService {

    // add crud operations


    private final AllergenRepository allergenRepository;

    @Cacheable( cacheNames = "allergens", key = "#name")
    public Allergen findOrCreateAllergen(String name){

        Optional<Allergen> dbAllergen = allergenRepository.findByName(name);

        if(! dbAllergen.isPresent()){
            Allergen newAllergen = new Allergen();
            newAllergen.setName(name);
            allergenRepository.save(newAllergen);
            dbAllergen = allergenRepository.findByName(name);
        }

        return dbAllergen.get();

    }









}
