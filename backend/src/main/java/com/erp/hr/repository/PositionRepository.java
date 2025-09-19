package com.erp.hr.repository;

import com.erp.hr.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 직급 레포지토리
 * 직급 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    /**
     * 직급 코드로 직급 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.positionCode = :positionCode AND p.isDeleted = false")
    Optional<Position> findByPositionCode(@Param("positionCode") String positionCode);

    /**
     * 회사별 직급 목록 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.company.id = :companyId AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 회사별 활성 직급 목록 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.company.id = :companyId AND p.isActive = true AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findActiveByCompanyId(@Param("companyId") Long companyId);

    // findByPositionLevel 메서드 제거됨 (positionLevel 필드가 DB 스키마에 없음)

    // findByPositionCategory 메서드 제거됨 (positionCategory 필드가 DB 스키마에 없음)

    // findByPositionType 메서드 제거됨 (positionType 필드가 DB 스키마에 없음)

    /**
     * 활성 직급 목록 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.isActive = true AND p.isDeleted = false " +
           "ORDER BY p.company.name, p.level")
    List<Position> findActivePositions();

    /**
     * 직급명으로 검색
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.name LIKE %:name% AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findByNameContaining(@Param("name") String name);

    /**
     * 회사별 직급명 검색
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.company.id = :companyId AND p.name LIKE %:name% AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findByCompanyIdAndNameContaining(@Param("companyId") Long companyId, 
                                                   @Param("name") String name);

    /**
     * 직급 검색 (페이징)
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE (p.name LIKE %:searchTerm% OR p.positionCode LIKE %:searchTerm%) " +
           "AND p.isDeleted = false")
    Page<Position> searchPositions(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 직급 검색 (페이징)
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.company.id = :companyId " +
           "AND (p.name LIKE %:searchTerm% OR p.positionCode LIKE %:searchTerm%) " +
           "AND p.isDeleted = false")
    Page<Position> searchPositionsByCompany(@Param("companyId") Long companyId, 
                                          @Param("searchTerm") String searchTerm, 
                                          Pageable pageable);

    /**
     * 직급 코드 중복 확인
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p " +
           "WHERE p.positionCode = :positionCode AND p.isDeleted = false")
    boolean existsByPositionCode(@Param("positionCode") String positionCode);

    /**
     * 회사별 직급 코드 중복 확인
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p " +
           "WHERE p.company.id = :companyId AND p.positionCode = :positionCode AND p.isDeleted = false")
    boolean existsByCompanyIdAndPositionCode(@Param("companyId") Long companyId, 
                                           @Param("positionCode") String positionCode);

    /**
     * 직급 코드 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p " +
           "WHERE p.positionCode = :positionCode AND p.id != :excludeId AND p.isDeleted = false")
    boolean existsByPositionCodeAndIdNot(@Param("positionCode") String positionCode, 
                                        @Param("excludeId") Long excludeId);

    /**
     * 회사별 직급 코드 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p " +
           "WHERE p.company.id = :companyId AND p.positionCode = :positionCode " +
           "AND p.id != :excludeId AND p.isDeleted = false")
    boolean existsByCompanyIdAndPositionCodeAndIdNot(@Param("companyId") Long companyId, 
                                                    @Param("positionCode") String positionCode, 
                                                    @Param("excludeId") Long excludeId);

    /**
     * 전체 직급 목록 조회 (페이징, 연관관계 포함)
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.isDeleted = false")
    Page<Position> findAllWithCompany(Pageable pageable);

    /**
     * 회사별 전체 직급 목록 조회 (페이징)
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.company.id = :companyId AND p.isDeleted = false")
    Page<Position> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 직급별 직원 수가 있는 직급 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE SIZE(p.employees) > 0 AND p.isDeleted = false " +
           "ORDER BY SIZE(p.employees) DESC")
    List<Position> findPositionsWithEmployees();

    /**
     * 직급별 직원 수가 없는 직급 조회
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE SIZE(p.employees) = 0 AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findPositionsWithoutEmployees();

    // findPromotablePositions 메서드 제거됨 (positionLevel 필드가 DB 스키마에 없음)

    // findTopLevelPositions 메서드 제거됨 (positionLevel 필드가 DB 스키마에 없음)

    // findBottomLevelPositions 메서드 제거됨 (positionLevel 필드가 DB 스키마에 없음)

    /**
     * 급여 범위별 직급 조회 (minSalary, maxSalary 필드 제거로 인해 비활성화)
     */
    @Query("SELECT p FROM Position p " +
           "JOIN FETCH p.company c " +
           "WHERE p.isActive = true AND p.isDeleted = false " +
           "ORDER BY p.level")
    List<Position> findBySalaryRange(@Param("salary") java.math.BigDecimal salary);

    // getPositionCountByCategory 메서드 제거됨 (positionCategory 필드가 DB 스키마에 없음)

    // getPositionCountByType 메서드 제거됨 (positionType 필드가 DB 스키마에 없음)

    /**
     * 회사별 직급 수 통계
     */
    @Query("SELECT c.name, COUNT(p) FROM Position p " +
           "JOIN p.company c " +
           "WHERE p.isDeleted = false " +
           "GROUP BY c.id, c.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getPositionCountByCompany();

    // getPositionCountByLevel 메서드 제거됨 (positionLevel 필드가 DB 스키마에 없음)

    /**
     * 활성/비활성 직급 수 통계
     */
    @Query("SELECT p.isActive, COUNT(p) FROM Position p " +
           "WHERE p.isDeleted = false " +
           "GROUP BY p.isActive")
    List<Object[]> getPositionCountByActiveStatus();
}