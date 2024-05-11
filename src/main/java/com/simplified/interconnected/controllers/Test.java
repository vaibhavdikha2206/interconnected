package com.simplified.interconnected.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/interconnected")
public class Test {


    @GetMapping("/test2")
    public String getString() throws IOException {
        System.out.println("sending response");
        return "Test App New";
    }

}


