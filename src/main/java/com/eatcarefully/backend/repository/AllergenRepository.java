package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.Allergen;
import com.eatcarefully.backend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {

    Optional<Allergen> findByName(String name);
}
