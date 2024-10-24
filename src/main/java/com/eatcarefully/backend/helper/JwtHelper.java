package com.eatcarefully.backend.helper;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class JwtHelper {

    private static final String USERNAME_CLAIM = "preferred_username";

    public String getUsernameFromToken(Jwt jwt){
        return jwt.getClaim(USERNAME_CLAIM);
    }
}
