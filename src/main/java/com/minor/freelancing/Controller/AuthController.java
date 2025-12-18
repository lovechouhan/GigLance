package com.minor.freelancing.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.minor.freelancing.DTO.UserRegistrationDto;
import com.minor.freelancing.DTO.FreelancerRegistrationDto;
import com.minor.freelancing.DTO.ClientRegistrationDto;
import com.minor.freelancing.Services.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf login.html
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler(Model model) {
        model.addAttribute("error", "Credentials Invalid !!");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        return "register-choose";
    }

    @GetMapping("/register/freelancer")
    public String showFreelancerForm(Model model) {
        model.addAttribute("freelancerDto", new FreelancerRegistrationDto());
        return "register-freelancer";
    }

    @PostMapping("/register/freelancer")
    public String registerFreelancer(@ModelAttribute("freelancerDto") FreelancerRegistrationDto dto) {
        userService.registerFreelancer(dto);
        return "redirect:/login";
    }

    @GetMapping("/register/client")
    public String showClientForm(Model model) {
        model.addAttribute("clientDto", new ClientRegistrationDto());
        return "register-client";
    }

    @PostMapping("/register/client")
    public String registerClient(@ModelAttribute("clientDto") ClientRegistrationDto dto) {
        userService.registerClient(dto);
        return "redirect:/client/findfreelancers";
    }
}
