package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.userType = :userType ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndUserTypeOrderByCreatedAtDesc(Long userId, String userType);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.userType = :userType AND n.readStatus = false")
    long countByUserIdAndUserTypeAndReadStatusFalse(Long userId, String userType);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :id AND n.readStatus = false")
    public int findUnreadCountByUserId(Long id);

}
