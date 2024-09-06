package com.kinde.oauth.controller;

import com.kinde.oauth.service.KindeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class KindeController {

    private final KindeService kindeService;

    public KindeController(KindeService kindeService) {
        this.kindeService = kindeService;
    }

    @RequestMapping(path = {"/", "/home"}, method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    @GetMapping(path = "/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("kindeUser", kindeService.loadDashboard(SecurityContextHolder.getContext().getAuthentication()));
        return "dashboard";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public String adminEndpoint() {
        return "admin";
    }

    @GetMapping("/read")
    @PreAuthorize("hasRole('read')")
    public String readEndpoint() {
        return "read";
    }

    @GetMapping("/write")
    @PreAuthorize("hasRole('write')")
    public String writeEndpoint() {
        return "write";
    }
}
