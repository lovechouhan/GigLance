package com.minor.freelancing.CommonMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Services.ImageServices;
import com.minor.freelancing.Services.UserService;

@Service
public class CommonMethodService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageServices cloudinaryService;

    public User AuthenticateUser() {
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
        return user;
    }

    public String uploadImageToCloudinary(MultipartFile profileImage) {
        try {
            String imageUrl = cloudinaryService.uploadFile(profileImage);
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
