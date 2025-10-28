package com.erp.common.repository;

import com.erp.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 레포지토리
 * 사용자 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명으로 사용자 조회 (회사 및 부서 정보 포함)
     * SUPER_ADMIN은 company가 NULL일 수 있으므로 LEFT JOIN 사용
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.username = :username AND u.isDeleted = false")
    Optional<User> findByUsernameWithCompanyAndDepartment(@Param("username") String username);

    /**
     * ID로 사용자 조회 (회사 및 부서 정보 포함)
     * SUPER_ADMIN은 company가 NULL일 수 있으므로 LEFT JOIN 사용
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findByIdWithCompanyAndDepartment(@Param("id") Long id);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일로 사용자 조회 (회사 및 부서 정보 포함)
     * SUPER_ADMIN은 company가 NULL일 수 있으므로 LEFT JOIN 사용
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmailWithCompanyAndDepartment(@Param("email") String email);

    /**
     * 사용자명으로 활성 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true AND u.isDeleted = false")
    Optional<User> findActiveByUsername(@Param("username") String username);

    /**
     * 이메일로 활성 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true AND u.isDeleted = false")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * 회사별 사용자 목록 조회
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c JOIN FETCH u.department d WHERE u.company.id = :companyId AND u.isDeleted = false")
    List<User> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 부서별 사용자 목록 조회
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c JOIN FETCH u.department d WHERE u.department.id = :departmentId AND u.isDeleted = false")
    List<User> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 역할별 사용자 목록 조회
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.role = :role AND u.isDeleted = false")
    List<User> findByRole(@Param("role") User.UserRole role);

    /**
     * 활성 상태별 사용자 조회
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.isActive = :isActive AND u.isDeleted = false")
    List<User> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * 사용자명 또는 실명으로 검색 (페이징)
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH u.department d " +
           "WHERE (u.username LIKE %:searchTerm% OR u.fullName LIKE %:searchTerm%) AND u.isDeleted = false")
    Page<User> searchByUsernameOrFullName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 사용자 검색 (페이징)
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH u.department d " +
           "WHERE u.company.id = :companyId AND (u.username LIKE %:searchTerm% OR u.fullName LIKE %:searchTerm%) AND u.isDeleted = false")
    Page<User> searchByCompanyAndTerm(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 사용자명 중복 확인
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(String email);

    /**
     * 사용자명 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 이메일 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 전체 사용자 목록 조회 (페이징, 연관관계 포함)
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH u.department d WHERE u.isDeleted = false")
    Page<User> findAllWithCompanyAndDepartment(Pageable pageable);

    /**
     * 마지막 로그인 시간 업데이트
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * 비밀번호 변경 시간 업데이트
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordChangedAt = :changeTime WHERE u.id = :userId")
    void updatePasswordChangedAt(@Param("userId") Long userId, @Param("changeTime") LocalDateTime changeTime);

    /**
     * 계정 잠금 상태 변경
     */
    @Modifying
    @Query("UPDATE User u SET u.isLocked = :isLocked WHERE u.id = :userId")
    void updateAccountLockStatus(@Param("userId") Long userId, @Param("isLocked") Boolean isLocked);

    /**
     * 계정 활성화 상태 변경
     */
    @Modifying
    @Query("UPDATE User u SET u.isActive = :isActive WHERE u.id = :userId")
    void updateAccountActiveStatus(@Param("userId") Long userId, @Param("isActive") Boolean isActive);

    /**
     * 특정 기간 동안 로그인하지 않은 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :beforeDate OR u.lastLoginAt IS NULL")
    List<User> findUsersNotLoggedInSince(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * 비밀번호 만료된 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.passwordChangedAt < :beforeDate OR u.passwordChangedAt IS NULL")
    List<User> findUsersWithExpiredPassword(@Param("beforeDate") LocalDateTime beforeDate);
}

