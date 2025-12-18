package com.minor.freelancing.Helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


public class Helper {

     public static String getEmailofLoggedInUser(Authentication authentication) {
        
  
       if(authentication instanceof OAuth2AuthenticationToken){

          var aOAuth2AuthenticationToken =  (OAuth2AuthenticationToken)authentication;
              var provider = aOAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
              var oauth2User = aOAuth2AuthenticationToken.getPrincipal();
              String username = "";
       
        // agr google se login kiya hai toh : email kaise nikalege
        if(provider.equalsIgnoreCase("google")){
            
              username = oauth2User.getAttribute("email").toString();  
        }

        return username;
    
        }


        // agr email & password se login kiya hai toh : email kaise nikalege
        else{
            return authentication.getName(); // or your actual logic to get email
        }
    }
}
