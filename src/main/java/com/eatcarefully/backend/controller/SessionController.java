package com.eatcarefully.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/session")
public class SessionController {

    @GetMapping("/logout")
    public String logout() {
        return "redirect:http://localhost:8080/realms/eat-carefully/protocol/openid-connect/logout";
    }

    @GetMapping("/user")
    public Principal getUser(Principal principal) {
        return principal;
    }
}
