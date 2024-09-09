/*
 * Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kinde.spring.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Controller
public class WelcomeController {

    @RequestMapping(path = {"/"}, method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(path = {"/home"}, method = RequestMethod.GET)
    public String home(Principal principal, Model model) {
        model.addAttribute("given_name", principal.getName());
        model.addAttribute("family_name", principal.getName());
        model.addAttribute("email", principal.getName());
        model.addAttribute("picture", principal.getName());
        return "home";
    }

    public static class Welcome {
        public String messageOfTheDay;
        public String username;

        public Welcome() {}

        public Welcome(String messageOfTheDay, String username) {
            this.messageOfTheDay = messageOfTheDay;
            this.username = username;
        }
    }

    @GetMapping("/everyone")
    @PreAuthorize("hasAuthority('Everyone')")
    public String everyoneRole() {
        return "Okta Groups have been mapped to Spring Security authorities correctly!";
    }
}
