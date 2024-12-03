package com.eatcarefully.backend.service;


import com.eatcarefully.backend.model.PreferenceName;
import com.eatcarefully.backend.repository.PreferenceNameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PreferenceNamesService {

    private PreferenceNameRepository preferenceNameRepository;

    public PreferenceName createPreferenceName(String name){

        if(preferenceNameRepository.findByName(name).isPresent())
            throw new IllegalArgumentException("Sucha preference name already exists");

        PreferenceName preferenceName = new PreferenceName();
        preferenceName.setName(name);

        return preferenceNameRepository.save(preferenceName);

    }
    public PreferenceName getPreferenceName(String name){

        return preferenceNameRepository.findByName(name).orElse(null);

    }

    public List<String> getAllPreferenceNames(){

        return preferenceNameRepository.findAll().stream().map(PreferenceName::getName).toList();
    }

    public void removePreferenceName(String name){
        preferenceNameRepository.findByName(name).ifPresent(prefName -> preferenceNameRepository.delete(prefName));

    }




}
