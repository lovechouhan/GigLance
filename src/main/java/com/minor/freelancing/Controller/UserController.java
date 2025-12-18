package com.minor.freelancing.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.minor.freelancing.Services.UserService;

@Controller

public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/chooserole")
    public String chooseRole() {
        return "register-choose";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({ "/", "/home" })
    public String home() {
        return "home";
    }
}
