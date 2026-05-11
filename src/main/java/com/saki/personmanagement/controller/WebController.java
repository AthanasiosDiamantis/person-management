package com.saki.personmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web Controller for Thymeleaf template rendering.
 *
 * <p>Note the difference: @Controller returns view names, @RestController returns JSON.</p>
 *
 * @author saki
 */
@Controller
public class WebController {

    /**
     * Shows the login page.
     */
    @GetMapping("/login")
    public String loging() {
        return "login"; // -> renders tamplates/login.html
    }

    @GetMapping("/dashboard")
    public String persons() {
        return "persons"; // → renders templates/persons.html
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
