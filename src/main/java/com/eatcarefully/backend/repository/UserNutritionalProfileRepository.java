package com.eatcarefully.backend.repository;


import com.eatcarefully.backend.model.UserNutritionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNutritionalProfileRepository extends JpaRepository<UserNutritionalProfile, String> {

}
