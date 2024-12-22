package com.ptit.e_commerce_website_be.do_an_nhom.services.notification;

import com.ptit.e_commerce_website_be.do_an_nhom.repositories.InventoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    @Override
    public Long getQuantityNewNotification(Long userId) {
        return notificationRepository.getQuantityNewNotification(userId);
    }
}
