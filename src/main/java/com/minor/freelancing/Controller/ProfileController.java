package com.minor.freelancing.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Services.ClientServices;
import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.UserService;

@Controller
@RequestMapping("/user")
public class ProfileController {

    private final UserService userService;
    private final ClientServices clientService;
    private final FreelancerServices freelancerService;

    public ProfileController(UserService userService, ClientServices clientService, FreelancerServices freelancerService) {
        this.userService = userService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
          User user = userService.findByEmail(email);
        if (user == null) {
            System.out.println("Authenticated user not found in database: " + email);
           
        }

        String role = user.getRole(); // "CLIENT" or "FREELANCER"
        if(role.equals("ROLE_CLIENT")) {
            Client client = clientService.findById(user.getId());
            model.addAttribute("client", client);
            model.addAttribute("user", user);
            return "client/profile";
        } else if(role.equals("ROLE_FREELANCER")) {
            Freelancer freelancer = freelancerService.findById(user.getId());
            model.addAttribute("freelancer", freelancer);
            model.addAttribute("user", user);
            return "freelancer/profile";
        }
        else {
            System.out.println("Unknown role for user: " + email);
            return "error"; // or some error page
        }
       
       
       
    }
}
