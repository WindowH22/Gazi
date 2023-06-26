package com.example.gazi.repository;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Notification;
import com.example.gazi.domain.NotificationEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByMemberAndNotificationEnum(Member member, NotificationEnum notificationEnum, Pageable pageable);
}
