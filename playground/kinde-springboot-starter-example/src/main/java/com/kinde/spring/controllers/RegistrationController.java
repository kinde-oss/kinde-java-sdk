package com.kinde.spring.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RegistrationController {

    @GetMapping("/registration")
    public RedirectView handleRegistration() {
        // Redirect to the OAuth 2.0 authorization URL with custom registration flow
        return new RedirectView("/oauth2/authorization/kinde?prompt=create");
    }
}