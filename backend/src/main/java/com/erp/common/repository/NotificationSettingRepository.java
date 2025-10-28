package com.erp.common.repository;

import com.erp.common.entity.NotificationScope;
import com.erp.common.entity.NotificationSetting;
import com.erp.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 알림 설정 레포지토리
 * 알림 설정 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    /**
     * 회사별 역할별 알림 설정 조회
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company.id = :companyId AND ns.role = :role AND ns.isDeleted = false")
    List<NotificationSetting> findByCompanyIdAndRole(@Param("companyId") Long companyId, @Param("role") User.UserRole role);

    /**
     * 시스템 전체 기본 설정 조회
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company IS NULL AND ns.role = :role AND ns.isDeleted = false")
    List<NotificationSetting> findSystemDefaultByRole(@Param("role") User.UserRole role);

    /**
     * 특정 설정 조회
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company.id = :companyId AND ns.role = :role AND ns.scope = :scope AND ns.isDeleted = false")
    Optional<NotificationSetting> findByCompanyAndRoleAndScope(
        @Param("companyId") Long companyId,
        @Param("role") User.UserRole role,
        @Param("scope") NotificationScope scope
    );

    /**
     * 시스템 기본 설정 조회
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company IS NULL AND ns.role = :role AND ns.scope = :scope AND ns.isDeleted = false")
    Optional<NotificationSetting> findSystemDefaultByRoleAndScope(
        @Param("role") User.UserRole role,
        @Param("scope") NotificationScope scope
    );

    /**
     * 회사별 모든 설정 조회
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company.id = :companyId AND ns.isDeleted = false ORDER BY ns.role, ns.scope")
    List<NotificationSetting> findAllByCompanyId(@Param("companyId") Long companyId);

    /**
     * 시스템 전체 기본 설정 조회 (모든 역할)
     */
    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.company IS NULL AND ns.isDeleted = false ORDER BY ns.role, ns.scope")
    List<NotificationSetting> findAllSystemDefaults();
}


