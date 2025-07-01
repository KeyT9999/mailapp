package com.outlookmail.mailapp.repository;

import com.outlookmail.mailapp.model.OtpRequest;
import com.outlookmail.mailapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long> {
    
    List<OtpRequest> findByUserOrderByRequestTimeDesc(User user);
    
    long countByUser(User user);
    
    @Query("SELECT o.user, COUNT(o) as requestCount, MAX(o.requestTime) as lastRequest FROM OtpRequest o GROUP BY o.user ORDER BY COUNT(o) DESC")
    List<Object[]> countRequestsByUser();
    
    @Query("SELECT o.user, COUNT(o) as requestCount FROM OtpRequest o WHERE o.user = :user GROUP BY o.user")
    Object[] countRequestsBySpecificUser(@Param("user") User user);
} 