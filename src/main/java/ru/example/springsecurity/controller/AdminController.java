package ru.example.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.springsecurity.models.Role;
import ru.example.springsecurity.models.User;
import ru.example.springsecurity.repo.RoleRepo;
import ru.example.springsecurity.repo.UserRepo;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminHomeAndAllUsers(Model model, Principal principal) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("principal", principal);
        model.addAttribute("userPrincipal", userRepo.findByUsername(principal.getName()));
        return "users";
    }

    @PostMapping
    public String adminHomePost(@RequestParam("id") Long id, @RequestParam("action") String action) {
        User user = userRepo.findById(id).get();
        if (action.equals("delete")) {
            userRepo.delete(user);
        } else if (action.equals("edit")) {
            return "redirect:/admin/update?id=" + user.getId();
        }
        return "redirect:/admin";
    }


    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepo.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, @RequestParam("authorities") List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            user.setRoles(new HashSet<>(roleRepo.findAll()));
        } else {
            Set<Role> roleSet = new HashSet<>(roleRepo.getRolesByName("ROLE_USER"));
            user.setRoles(roleSet);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String edit(Model model, @RequestParam("id") Long id) {
        User user = userRepo.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("isAdmin", user.getAuthorities().contains("ROLE_ADMIN"));
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") User user, @RequestParam("id") Long id, @RequestParam("role") List<String> roles) {
        User updatedUser = userRepo.findById(id).stream()
                .peek(up -> {
                    up.setUsername(user.getUsername());
                    up.setPassword(passwordEncoder.encode(user.getPassword()));
                    up.setEmail(user.getEmail());
                    if (roles.contains("ROLE_ADMIN")) {
                        up.setRoles(new HashSet<>(roleRepo.findAll()));
                    } else {
                        up.setRoles(new HashSet<>(Collections.singleton(roleRepo.findByName("ROLE_USER"))));
                    }
                }).findFirst().get();
        userRepo.save(updatedUser);
        return "redirect:/admin";
    }
}
