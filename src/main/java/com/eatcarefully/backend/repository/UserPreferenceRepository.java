package com.eatcarefully.backend.repository;

import com.eatcarefully.backend.model.UserPreference;
import com.eatcarefully.backend.model.UserPreferenceCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UserPreferenceCompositeKey> {


}
