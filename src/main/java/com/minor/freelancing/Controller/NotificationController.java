package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.Entities.Notification;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Services.NotificationService;
import com.minor.freelancing.Services.UserService;

@Controller
@RequestMapping("/user")
public class NotificationController {

    @Autowired
    private CommonMethodService commonMethodService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public String userNotifications(Model model) {

        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login"; // or some error page
        }
        User user = userService.findByEmail(loggedUser.getEmail());
        String userType = user.getRole().toString(); // "CLIENT" or "FREELANCER"
        List<Notification> notifications = notificationService.getUserNotificationsWithRole(user.getId(), userType);

        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", user);
        if (userType.equals("ROLE_CLIENT")) {
            return "redirect:/client/notifications/" + user.getId();

        } else if (userType.equals("ROLE_FREELANCER")) {
            return "redirect:/freelancer/notifications/" + user.getId();
        } else {
            return "error/unauthorized";
        }
    }
}
