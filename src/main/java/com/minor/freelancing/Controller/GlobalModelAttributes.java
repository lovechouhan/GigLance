package com.minor.freelancing.Controller;

import com.minor.freelancing.Services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.Entities.User;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonMethodService commonMethodService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
       
        User user  = commonMethodService.AuthenticateUser();
        if(user==null) System.out.println("User is null in GlobalModelAttributes");
        if(user != null){
            int unread = notificationService.getUnreadCount(user.getId());
            model.addAttribute("globalUnread", unread);
        }
    }
}

