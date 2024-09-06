package com.kinde.oauth.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class KindeErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }

    @RequestMapping("/403")
    public String handle403() {
        return "403";
    }

    @RequestMapping("/404")
    public String handle404() {
        return "404";
    }

    @RequestMapping("/500")
    public String handle500() {
        return "500";
    }
}

