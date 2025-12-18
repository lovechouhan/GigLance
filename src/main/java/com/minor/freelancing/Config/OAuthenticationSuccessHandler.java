package com.minor.freelancing.Config;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Helper.Provider;
import com.minor.freelancing.Helper.Roles;
import com.minor.freelancing.Repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// for oAuth2 login success handler

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository userRepo;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String targetUrl = "/home";

        // ----------------------------------------------
        // CASE 1: NORMAL LOGIN (email/password)
        // ----------------------------------------------
        if (authentication instanceof UsernamePasswordAuthenticationToken) {

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority auth : authorities) {
                String role = auth.getAuthority();

                if (auth.getAuthority().equals("ROLE_CLIENT")) {
                    // Get the username (email) from the principal
                    String email = authentication.getName();
                    User user = userRepo.findByEmail(email);

                    if (user != null && user.getId() != null) {
                        targetUrl = "/client/dashboard/" + user.getId();
                    } else {
                        targetUrl = "/client/findfreelancers"; // fallback
                    }
                } else if (auth.getAuthority().equals("ROLE_FREELANCER")) {
                    // Get the username (email) from the principal
                    String email = authentication.getName();
                    User user = userRepo.findByEmail(email);
                    if (user != null && user.getId() != null) {
                        targetUrl = "/freelancer/dashboard/" + user.getId();
                    } else {
                        targetUrl = "/freelancer/findProjects"; // fallback
                    }
                }
                else if (auth.getAuthority().equals("ROLE_ADMIN")) {
                     String email = authentication.getName();
                    User user = userRepo.findByEmail(email);
                    targetUrl = "/admin/dashboard/"+ user.getId();
                }
            }

            redirectStrategy.sendRedirect(request, response, targetUrl);
            return;
        }

        // ----------------------------------------------
        // CASE 2: GOOGLE OAUTH2 LOGIN
        // ----------------------------------------------
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

            String provider = oauthToken.getAuthorizedClientRegistrationId();
            DefaultOAuth2User oAuthUser = (DefaultOAuth2User) oauthToken.getPrincipal();

            String email = oAuthUser.getAttribute("email");
            String name = oAuthUser.getAttribute("name");

            User user = userRepo.findByEmail(email);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole(Roles.CLIENT); // default new user → client
                user.setEnabled(true);
                user.setProvider(Provider.GOOGLE);
                user.setImageUrl(oAuthUser.getAttribute("picture"));

                userRepo.save(user);
            }

            // session attributes
            request.getSession().setAttribute("userId", user.getId());
            request.getSession().setAttribute("userRole", user.getRole());

            String role = user.getRole().toUpperCase();

            if (role.contains("CLIENT")) {
                targetUrl = "/client/findfreelancers";
            } else if (role.contains("FREELANCER")) {
                targetUrl = "/freelancer/dashboard/" + user.getId();
            }

            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}