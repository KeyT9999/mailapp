package com.outlookmail.mailapp.repository;

import com.outlookmail.mailapp.model.User;
import com.outlookmail.mailapp.model.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {
    List<UserLoginHistory> findByUserOrderByLoginTimeDesc(User user);
    List<UserLoginHistory> findByUserOrderByLoginTimeAsc(User user);

    @Query("SELECT COUNT(DISTINCT h.ipAddress) FROM UserLoginHistory h WHERE h.user = :user")
    long countDistinctIpByUser(@Param("user") User user);
} 