package com.kinde.oauth.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling common error pages in the application.
 */
@Controller
public class KindeErrorController implements ErrorController {

    /**
     * Handles general errors by returning the "error" page.
     *
     * @return the name of the "error" view
     */
    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }

    /**
     * Handles 403 Forbidden errors by returning the "403" page.
     *
     * @return the name of the "403" view
     */
    @RequestMapping("/403")
    public String handle403() {
        return "403";
    }

    /**
     * Handles 404 Not Found errors by returning the "404" page.
     *
     * @return the name of the "404" view
     */
    @RequestMapping("/404")
    public String handle404() {
        return "404";
    }

    /**
     * Handles 500 Internal Server errors by returning the "500" page.
     *
     * @return the name of the "500" view
     */
    @RequestMapping("/500")
    public String handle500() {
        return "500";
    }
}

