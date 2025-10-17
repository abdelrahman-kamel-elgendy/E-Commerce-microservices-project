package com.e_commerce.auth_service.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.e_commerce.auth_service.models.User;
import com.e_commerce.auth_service.models.UserSession;

import feign.Param;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserEmailAndActiveTrue(String email);

    List<UserSession> findByUserEmail(String email);

    long countByUserEmailAndActiveTrue(String email);

    Optional<UserSession> findBySessionIdAndActiveTrue(Long sessionId);

    @Modifying
    @Query("UPDATE UserSession us SET us.active = false, us.loggedOutAt = :now WHERE us.sessionId = :sessionId")
    void logoutSession(@Param("sessionId") String sessionId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE UserSession us SET us.active = false, us.loggedOutAt = :now WHERE us.user = :user AND us.active = true")
    void logoutAllUserSessions(@Param("user") User user, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") Instant now);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.expiresAt < :now")
    long countExpiredSessions(@Param("now") Instant now);

    @Query("SELECT COUNT(us) FROM UserSession us WHERE us.user = :user AND us.active = true")
    long countActiveSessionsByUser(@Param("user") User user);
}