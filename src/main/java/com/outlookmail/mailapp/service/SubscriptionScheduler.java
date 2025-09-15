package com.outlookmail.mailapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionScheduler {
    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Run every day at 08:00 server time (T-1 reminders & forecast to admin)
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyExpiryReminders() {
        subscriptionService.notifyCustomersEndingTomorrow();
    }

    // Run every day at 08:05 server time (T0 digest to admin)
    @Scheduled(cron = "0 5 8 * * *")
    public void sendTodayExpiryAdminDigest() {
        subscriptionService.sendAdminDigestForToday();
    }
} 