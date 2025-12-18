package com.minor.freelancing.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Notification;
import com.minor.freelancing.Repositories.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repo;

    public void notifyClient(Long clientId,Long submissionId, String title, String msg) {
        Notification n = new Notification();
        n.setUserId(clientId);
        n.setUserType("CLIENT");
        n.setTitle(title);
        n.setMessage(msg);
        n.setRelatedSubmissionId(submissionId);
        
        repo.save(n);
    }

    public void notifyFreelancer(Long freelancerId, String title, String msg) {
        Notification n = new Notification();
        n.setUserId(freelancerId);
        n.setUserType("FREELANCER");
        n.setTitle(title);
        n.setMessage(msg);
        repo.save(n);
    }

    public List<Notification> getUserNotificationsWithRole(Long id, String userType) {
        return repo.findByUserIdAndUserTypeOrderByCreatedAtDesc(id, userType);
    }

    // public void markClientNotificationsAsRead(Long clientId) {
    // List<Notification> notifications =
    // repo.findByUserIdAndUserTypeAndReadStatusFalse(clientId, "CLIENT");
    // for (Notification n : notifications) {
    // n.setReadStatus(true);
    // }
    // repo.saveAll(notifications);
    // }

    public void markAsRead(Long notifId) {
        Notification notification = repo.findById(notifId).orElse(null);
        if (notification != null) {
            notification.setReadStatus(true);
            repo.save(notification);
        }
    }

    public List<Notification> getClientNotifications(Long id) {
        return repo.findByUserIdAndUserTypeOrderByCreatedAtDesc(id, "CLIENT");
    }

    public long getClientUnreadCount(Long id) {
        return repo.countByUserIdAndUserTypeAndReadStatusFalse(id, "CLIENT");
    }

    public List<Notification> getfreelancerNotifications(Long id, String string) {
        return repo.findByUserIdAndUserTypeOrderByCreatedAtDesc(id, string);
    }

    public Long getfreelancerUnreadCount(Long id) {
        return repo.countByUserIdAndUserTypeAndReadStatusFalse(id, "FREELANCER");
    }

    public void deleteNotification(Long notificationId) {
        repo.deleteById(notificationId);
    }

    public Notification getNotificationById(Long Id){
        return repo.findById(Id).orElse(null);
    }

    public int getUnreadCount(Long id) {
      return repo.findUnreadCountByUserId(id);
    }

    public void notifyClientforProposals(Long clientId, String new_Proposal_Submitted, String a_new_proposal_has_been_submitted_for_you) {
        Notification n = new Notification();
        n.setUserId(clientId);
        n.setUserType("CLIENT");
        n.setTitle(new_Proposal_Submitted);
        n.setMessage(a_new_proposal_has_been_submitted_for_you);
        
        repo.save(n);
    }

}
