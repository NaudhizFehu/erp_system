package com.erp.common.repository;

import com.erp.common.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 알림 레포지토리
 * 알림 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 사용자별 알림 목록 조회 (최신순)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isDeleted = false ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자별 읽지 않은 알림 개수 조회
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.isDeleted = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 사용자별 읽지 않은 알림 목록 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.isDeleted = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * 사용자별 특정 타입의 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type AND n.isDeleted = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Notification.NotificationType type);

    /**
     * 사용자별 최근 알림 조회 (지정된 개수만큼)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isDeleted = false ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자별 최근 3일 이내 알림 조회 (읽은/읽지 않은 모든 알림)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isDeleted = false AND n.createdAt >= :threeDaysAgo ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") Long userId, @Param("threeDaysAgo") java.time.LocalDateTime threeDaysAgo);

    /**
     * 사용자별 최근 2주 이내 알림 조회 (읽은/읽지 않은 모든 알림)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isDeleted = false AND n.createdAt >= :twoWeeksAgo ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserIdAndDateRange(@Param("userId") Long userId, @Param("twoWeeksAgo") java.time.LocalDateTime twoWeeksAgo);

    /**
     * 사용자별 모든 알림 조회 (삭제되지 않은 것만)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isDeleted = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);
}
