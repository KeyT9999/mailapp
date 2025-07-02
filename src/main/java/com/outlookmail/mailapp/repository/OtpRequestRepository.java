package com.outlookmail.mailapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.outlookmail.mailapp.model.OtpRequest;
import com.outlookmail.mailapp.model.User;

@Repository
public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long> {
    
    List<OtpRequest> findByUserOrderByRequestTimeDesc(User user);
    
    long countByUser(User user);
    
    @Query("SELECT o.user, COUNT(o) as requestCount, MAX(o.requestTime) as lastRequest FROM OtpRequest o GROUP BY o.user ORDER BY COUNT(o) DESC")
    List<Object[]> countRequestsByUser();
    
    @Query("SELECT o.user, COUNT(o) as requestCount FROM OtpRequest o WHERE o.user = :user GROUP BY o.user")
    Object[] countRequestsBySpecificUser(@Param("user") User user);
    
    @Query("SELECT DISTINCT o.ipAddress FROM OtpRequest o WHERE o.user = :user AND o.ipAddress IS NOT NULL ORDER BY o.requestTime ASC")
    List<String> findFirst2DistinctIpByUser(@Param("user") User user);
} 