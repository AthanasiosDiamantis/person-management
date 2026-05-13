package com.saki.personmanagement.controller;

import com.saki.personmanagement.model.User;
import com.saki.personmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Admin controller - only accessible for ROLE_ADMIN.
 *
 * Access is controlled by SecurityConfig: .requestMatchers("/admin/**").hasRole("ADMIN")
 *
 * @author saki
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    /**
     * Shows admin dashboard with user count.
     * Zeigt Admin-Dashboard mit User-Anzahl.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long userCount = userRepository.count();
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("ADMIN"))
                .count();

        model.addAttribute("userCount", userCount);
        model.addAttribute("adminCount", adminCount);
        return "admin/dashboard";
    }

    /**
     * Shows list of all users - admin only!
     * Zeigt Liste aller User - nur für Admins!
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

}
