package com.eatcarefully.backend.controllers;


import com.eatcarefully.backend.controller.ProductController;
import com.eatcarefully.backend.controller.UserProfileAndPreferencesController;
import com.eatcarefully.backend.dto.ScanResponseDTO;
import com.eatcarefully.backend.dto.UserThresholdAndPreferencesDTO;
import com.eatcarefully.backend.helper.JwtHelper;
import com.eatcarefully.backend.service.ProductService;
import com.eatcarefully.backend.service.UserPreferenceAndNutritionalProfileService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileAndPreferencesController.class)
public class PreferencesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPreferenceAndNutritionalProfileService preferenceService;

    @MockBean
    private JwtHelper jwtHelper;


    @Test
    public void Should_ReturnOK_When_UserExists() throws Exception {

        when(jwtHelper.getUsernameFromToken(any())).thenReturn("test");
        when(preferenceService.getOrCreateThresholdsAndPreferences(any())).thenReturn(new UserThresholdAndPreferencesDTO());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/user-profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

    }


    @Test
    public void Should_ReturnNotFound_When_UserDoesNotExists() throws Exception {

        when(jwtHelper.getUsernameFromToken(any())).thenReturn(null);
        when(preferenceService.getOrCreateThresholdsAndPreferences(any())).thenThrow(new IllegalArgumentException());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/user-profile")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNotFound());


    }


    @Test
    public void Should_ReturnOK_When_UpdateSuccessful() throws Exception {


        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("thresholds", null);
        jsonPayload.put("preferences", null);

        when(jwtHelper.getUsernameFromToken(any())).thenReturn("test");
        when(preferenceService.updateUserPreferencesAndThresholds(any(), any())).thenReturn(new UserThresholdAndPreferencesDTO());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user-profile/update")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(jsonPayload.toJSONString())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

    }

    @Test
    public void Should_ReturnNotFound_When_UpdateNotSuccessfulUserNotFound() throws Exception {

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("thresholds", null);
        jsonPayload.put("preferences", null);

        when(jwtHelper.getUsernameFromToken(any())).thenReturn("test");
        when(preferenceService.updateUserPreferencesAndThresholds(any(), any())).thenThrow(new IllegalArgumentException());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user-profile/update")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(String.valueOf(jsonPayload.toJSONString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    public void Should_ReturnBadRequest_When_NoDTOInBody() throws Exception {

        when(jwtHelper.getUsernameFromToken(any())).thenReturn("test");
        when(preferenceService.updateUserPreferencesAndThresholds(any(), any())).thenReturn(new UserThresholdAndPreferencesDTO());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user-profile/update")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isBadRequest());

    }










}
