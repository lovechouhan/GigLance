package com.minor.freelancing.Helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Repositories.UserRepository;
import com.minor.freelancing.Services.UserService;


import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import com.minor.freelancing.Controller.UserController;

@ControllerAdvice
@Component
public class MainHelper {
    


    private static final Logger logger = getLogger(UserController.class);

    
    @Autowired
    private UserRepository userRepository;

    // For Showing Every Details of User on every page
    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       if(authentication == null) return;
    
        System.out.println("Adding logged in user information to model");
        String username = Helper.getEmailofLoggedInUser(authentication);
        logger.info("Logged in user email: {}", username);
        
        User user = userRepository.findByEmail(username);
        String userRole = user.getRole();
        System.out.println("user's role: "+userRole);
        System.out.println("user 's info "+user);
        System.out.println("user's name: "+user.getName());
        System.out.println("user's email: "+user.getEmail());
        model.addAttribute("loggedInUser", user);  // user ki key ""loggedInUser"" hai
        model.addAttribute("userRole", userRole);
        if(userRole.equals("ROLE_FREELANCER")){
            model.addAttribute("isFreelancer", true);
           
        } else if(userRole.equals("ROLE_CLIENT")){
            model.addAttribute("isClient", true);
        }
        else{
            System.out.println("User role is neither CLIENT nor FREELANCER");
        }
        
    }



}
