package com.outlookmail.mailapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.outlookmail.mailapp.model.ServiceSubscription;

@Repository
public interface ServiceSubscriptionRepository extends JpaRepository<ServiceSubscription, Long> {
    List<ServiceSubscription> findByEndDate(LocalDate endDate);

    @Query("SELECT s FROM ServiceSubscription s WHERE s.endDate = :date AND s.preExpiryNotified = false")
    List<ServiceSubscription> findEndingOnAndNotNotified(@Param("date") LocalDate date);

    @Query("SELECT s FROM ServiceSubscription s WHERE s.endDate BETWEEN :start AND :end")
    List<ServiceSubscription> findEndingBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
} 