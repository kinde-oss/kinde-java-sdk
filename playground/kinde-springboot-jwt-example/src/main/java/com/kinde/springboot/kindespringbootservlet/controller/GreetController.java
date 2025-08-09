package com.kinde.springboot.kindespringbootservlet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/greet/{name}")
public class GreetController {
    @GetMapping
    public Map<String, String> greet(@PathVariable String name) {
        return Map.of("data", "Hello " + name);
    }
}
