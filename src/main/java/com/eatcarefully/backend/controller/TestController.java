package com.eatcarefully.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping(path = "/hello")
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.ok("Hello there");
    }
}
