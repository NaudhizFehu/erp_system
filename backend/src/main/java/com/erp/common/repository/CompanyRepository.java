package com.erp.common.repository;

import com.erp.common.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회사 레포지토리
 * 회사 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 회사 코드로 회사 조회
     */
    Optional<Company> findByCompanyCode(String companyCode);

    /**
     * 사업자등록번호로 회사 조회
     */
    Optional<Company> findByBusinessNumber(String businessNumber);

    /**
     * 법인등록번호로 회사 조회
     */
    Optional<Company> findByCorporationNumber(String corporationNumber);

    /**
     * 회사명으로 검색
     */
    @Query("SELECT c FROM Company c WHERE c.name LIKE %:name% AND c.isDeleted = false")
    List<Company> findByNameContaining(@Param("name") String name);

    /**
     * 활성 회사 목록 조회
     */
    @Query("SELECT c FROM Company c WHERE c.status = 'ACTIVE' AND c.isDeleted = false ORDER BY c.name")
    List<Company> findActiveCompanies();

    /**
     * 상태별 회사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.status = :status AND c.isDeleted = false")
    List<Company> findByStatus(@Param("status") Company.CompanyStatus status);

    /**
     * 회사 유형별 조회
     */
    @Query("SELECT c FROM Company c WHERE c.companyType = :companyType AND c.isDeleted = false")
    List<Company> findByCompanyType(@Param("companyType") Company.CompanyType companyType);

    /**
     * 회사명 또는 사업자등록번호로 검색 (페이징)
     */
    @Query("SELECT c FROM Company c WHERE (c.name LIKE %:searchTerm% OR c.businessNumber LIKE %:searchTerm%) AND c.isDeleted = false")
    Page<Company> searchByNameOrBusinessNumber(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사 코드 중복 확인
     */
    boolean existsByCompanyCode(String companyCode);

    /**
     * 사업자등록번호 중복 확인
     */
    boolean existsByBusinessNumber(String businessNumber);

    /**
     * 법인등록번호 중복 확인
     */
    boolean existsByCorporationNumber(String corporationNumber);

    /**
     * 회사 코드 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c WHERE c.companyCode = :companyCode AND c.id != :excludeId")
    boolean existsByCompanyCodeAndIdNot(@Param("companyCode") String companyCode, @Param("excludeId") Long excludeId);

    /**
     * 사업자등록번호 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c WHERE c.businessNumber = :businessNumber AND c.id != :excludeId")
    boolean existsByBusinessNumberAndIdNot(@Param("businessNumber") String businessNumber, @Param("excludeId") Long excludeId);

    /**
     * 법인등록번호 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c WHERE c.corporationNumber = :corporationNumber AND c.id != :excludeId")
    boolean existsByCorporationNumberAndIdNot(@Param("corporationNumber") String corporationNumber, @Param("excludeId") Long excludeId);

    /**
     * 전체 회사 목록 조회 (페이징, 삭제되지 않은 것만)
     */
    @Query("SELECT c FROM Company c WHERE c.isDeleted = false")
    Page<Company> findAllActive(Pageable pageable);

    /**
     * 설립일 범위로 회사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.establishedDate BETWEEN :startDate AND :endDate AND c.isDeleted = false")
    List<Company> findByEstablishedDateBetween(@Param("startDate") java.time.LocalDate startDate, 
                                              @Param("endDate") java.time.LocalDate endDate);

    /**
     * 직원 수 범위로 회사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.employeeCount BETWEEN :minCount AND :maxCount AND c.isDeleted = false")
    List<Company> findByEmployeeCountBetween(@Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);

    /**
     * 자본금 범위로 회사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.capitalAmount BETWEEN :minAmount AND :maxAmount AND c.isDeleted = false")
    List<Company> findByCapitalAmountBetween(@Param("minAmount") java.math.BigDecimal minAmount, 
                                           @Param("maxAmount") java.math.BigDecimal maxAmount);

    /**
     * 업종별 회사 조회
     */
    @Query("SELECT c FROM Company c WHERE c.businessType LIKE %:businessType% AND c.isDeleted = false")
    List<Company> findByBusinessTypeContaining(@Param("businessType") String businessType);

    /**
     * 지역별 회사 조회 (주소 기준)
     */
    @Query("SELECT c FROM Company c WHERE c.address LIKE %:region% AND c.isDeleted = false")
    List<Company> findByRegion(@Param("region") String region);

    /**
     * 회사 통계 조회 - 상태별 개수
     */
    @Query("SELECT c.status, COUNT(c) FROM Company c WHERE c.isDeleted = false GROUP BY c.status")
    List<Object[]> getCompanyCountByStatus();

    /**
     * 회사 통계 조회 - 유형별 개수
     */
    @Query("SELECT c.companyType, COUNT(c) FROM Company c WHERE c.isDeleted = false GROUP BY c.companyType")
    List<Object[]> getCompanyCountByType();
}




