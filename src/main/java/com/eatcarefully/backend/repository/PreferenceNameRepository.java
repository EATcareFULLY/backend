package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.PreferenceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenceNameRepository extends JpaRepository<PreferenceName, Long> {

    Optional<PreferenceName> findByName(String name);

}
