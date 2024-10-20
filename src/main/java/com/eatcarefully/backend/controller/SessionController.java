package com.eatcarefully.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/session")
public class SessionController {    //TODO: cleanup

    @GetMapping("/logout")
    public String logout() {
        return "redirect:http://localhost:8080/realms/eat-carefully/protocol/openid-connect/logout";
    }

    @ResponseBody
    @GetMapping("/user")
    public Principal getUser(Principal principal) {
        System.out.println("Principal: " + principal);
        return principal;
    }
}
