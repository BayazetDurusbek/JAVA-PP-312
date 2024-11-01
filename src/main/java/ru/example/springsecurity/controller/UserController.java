package ru.example.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.example.springsecurity.models.User;
import ru.example.springsecurity.repo.UserRepo;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/user")
    public String user(Principal principal, Model model) {
        User user = userRepo.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }
}
