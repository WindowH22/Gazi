package com.example.gazi.repository;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Notification;
import com.example.gazi.domain.enums.NotificationEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByMemberAndNotificationEnumIn(Member member, List<NotificationEnum> notificationEnums, Pageable pageable);
}
