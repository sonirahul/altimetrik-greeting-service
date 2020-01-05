package com.altimetrik.greetingservice.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/api")
public class GreetingsResource {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @GetMapping("/greet")
    public ResponseEntity<String> greet() {
        log.debug("calling greet method in greeting service");
        return ResponseEntity.ok("Greeting from the greeting service");
    }
}
