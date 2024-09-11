package com.kinde.oauth.controller;

import com.kinde.oauth.service.KindeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for handling Kinde OAuth-related endpoints and rendering views
 * for various roles and user actions.
 */
@Slf4j
@Controller
public class KindeController {

    private final KindeService kindeService;

    /**
     * Constructor for initializing the controller with the {@link KindeService}.
     *
     * @param kindeService the service used to handle Kinde OAuth operations.
     */
    public KindeController(KindeService kindeService) {
        this.kindeService = kindeService;
    }

    /**
     * Handles requests to the home page.
     *
     * @return the name of the "home" view.
     */
    @RequestMapping(path = {"/", "/home"}, method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    /**
     * Handles requests to the dashboard page, loading the authenticated user's Kinde profile.
     *
     * @param model the {@link Model} used to pass attributes to the view.
     * @return the name of the "dashboard" view.
     */
    @GetMapping(path = "/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("kindeUser", kindeService.loadDashboard());
        return "dashboard";
    }

    /**
     * Handles requests to the admin endpoint, restricted to users with the 'admin' role.
     *
     * @return the name of the "admin" view.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public String adminEndpoint() {
        return "admin";
    }

    /**
     * Handles requests to the read endpoint, restricted to users with the 'read' role.
     *
     * @return the name of the "read" view.
     */
    @GetMapping("/read")
    @PreAuthorize("hasRole('read')")
    public String readEndpoint() {
        return "read";
    }

    /**
     * Handles requests to the write endpoint, restricted to users with the 'write' role.
     *
     * @return the name of the "write" view.
     */
    @GetMapping("/write")
    // @PreAuthorize("hasRole('write')")
    public String writeEndpoint() {
        return "write";
    }
}
