package com.erp.hr.repository;

import com.erp.hr.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 급여 레포지토리
 * 급여 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    /**
     * 직원과 연월로 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.employee.id = :employeeId AND s.payYear = :year AND s.payMonth = :month")
    Optional<Salary> findByEmployeeIdAndYearMonth(@Param("employeeId") Long employeeId,
                                                 @Param("year") Integer year,
                                                 @Param("month") Integer month);

    /**
     * 직원별 급여 목록 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.employee.id = :employeeId " +
           "ORDER BY s.payYear DESC, s.payMonth DESC")
    List<Salary> findByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 직원별 연도별 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.employee.id = :employeeId AND s.payYear = :year " +
           "ORDER BY s.payMonth")
    List<Salary> findByEmployeeIdAndYear(@Param("employeeId") Long employeeId, 
                                        @Param("year") Integer year);

    /**
     * 연월별 전체 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "ORDER BY e.employeeNumber")
    List<Salary> findByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 회사별 연월별 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE e.company.id = :companyId AND s.payYear = :year AND s.payMonth = :month " +
           "ORDER BY e.employeeNumber")
    List<Salary> findByCompanyIdAndYearMonth(@Param("companyId") Long companyId,
                                           @Param("year") Integer year,
                                           @Param("month") Integer month);

    /**
     * 부서별 연월별 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE e.department.id = :departmentId AND s.payYear = :year AND s.payMonth = :month " +
           "ORDER BY e.employeeNumber")
    List<Salary> findByDepartmentIdAndYearMonth(@Param("departmentId") Long departmentId,
                                              @Param("year") Integer year,
                                              @Param("month") Integer month);

    /**
     * 급여 상태별 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.paymentStatus = :status " +
           "ORDER BY s.payYear DESC, s.payMonth DESC")
    List<Salary> findByPaymentStatus(@Param("status") Salary.PaymentStatus status);

    /**
     * 급여 유형별 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE s.salaryType = :type " +
           "ORDER BY s.payYear DESC, s.payMonth DESC")
    List<Salary> findBySalaryType(@Param("type") Salary.SalaryType type);

    /**
     * 지급 완료된 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE s.paymentStatus = 'PAID' " +
           "AND s.payDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.payDate DESC")
    List<Salary> findPaidSalariesByDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * 승인 대기 중인 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE s.paymentStatus = 'CALCULATED' " +
           "ORDER BY s.payYear DESC, s.payMonth DESC")
    List<Salary> findPendingApprovalSalaries();

    /**
     * 부서별 승인 대기 중인 급여 조회
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.department.id = :departmentId AND s.paymentStatus = 'CALCULATED' " +
           "ORDER BY s.payYear DESC, s.payMonth DESC")
    List<Salary> findPendingApprovalSalariesByDepartment(@Param("departmentId") Long departmentId);

    /**
     * 급여 검색 (페이징)
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE (e.name LIKE %:searchTerm% OR e.employeeNumber LIKE %:searchTerm%) " +
           "AND s.payYear = :year AND s.payMonth = :month")
    Page<Salary> searchSalaries(@Param("searchTerm") String searchTerm,
                               @Param("year") Integer year,
                               @Param("month") Integer month,
                               Pageable pageable);

    /**
     * 전체 급여 목록 조회 (페이징)
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab")
    Page<Salary> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 급여 목록 조회 (페이징)
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE e.company.id = :companyId")
    Page<Salary> findByCompanyIdWithDetails(@Param("companyId") Long companyId, 
                                           Pageable pageable);

    /**
     * 부서별 급여 목록 조회 (페이징)
     */
    @Query("SELECT s FROM Salary s " +
           "JOIN FETCH s.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH s.approvedBy ab " +
           "WHERE e.department.id = :departmentId")
    Page<Salary> findByDepartmentIdWithDetails(@Param("departmentId") Long departmentId, 
                                              Pageable pageable);

    /**
     * 급여 중복 확인
     */
    boolean existsByEmployeeIdAndPayYearAndPayMonth(Long employeeId, Integer payYear, Integer payMonth);

    /**
     * 연월별 총 급여 비용 계산
     */
    @Query("SELECT COALESCE(SUM(s.netPay), 0) FROM Salary s " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID'")
    BigDecimal sumNetPayByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 회사별 연월별 총 급여 비용 계산
     */
    @Query("SELECT COALESCE(SUM(s.netPay), 0) FROM Salary s " +
           "JOIN s.employee e " +
           "WHERE e.company.id = :companyId AND s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID'")
    BigDecimal sumNetPayByCompanyAndYearMonth(@Param("companyId") Long companyId,
                                             @Param("year") Integer year,
                                             @Param("month") Integer month);

    /**
     * 부서별 연월별 총 급여 비용 계산
     */
    @Query("SELECT COALESCE(SUM(s.netPay), 0) FROM Salary s " +
           "JOIN s.employee e " +
           "WHERE e.department.id = :departmentId AND s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID'")
    BigDecimal sumNetPayByDepartmentAndYearMonth(@Param("departmentId") Long departmentId,
                                                @Param("year") Integer year,
                                                @Param("month") Integer month);

    /**
     * 직원별 연간 총 급여 계산
     */
    @Query("SELECT COALESCE(SUM(s.netPay), 0) FROM Salary s " +
           "WHERE s.employee.id = :employeeId AND s.payYear = :year " +
           "AND s.paymentStatus = 'PAID'")
    BigDecimal sumNetPayByEmployeeAndYear(@Param("employeeId") Long employeeId, 
                                         @Param("year") Integer year);

    /**
     * 급여 범위별 직원 수 조회
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN s.netPay < 2000000 THEN '200만원 미만' " +
           "WHEN s.netPay < 3000000 THEN '200-300만원' " +
           "WHEN s.netPay < 4000000 THEN '300-400만원' " +
           "WHEN s.netPay < 5000000 THEN '400-500만원' " +
           "ELSE '500만원 이상' " +
           "END as salaryRange, COUNT(s) " +
           "FROM Salary s " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID' " +
           "GROUP BY " +
           "CASE " +
           "WHEN s.netPay < 2000000 THEN '200만원 미만' " +
           "WHEN s.netPay < 3000000 THEN '200-300만원' " +
           "WHEN s.netPay < 4000000 THEN '300-400만원' " +
           "WHEN s.netPay < 5000000 THEN '400-500만원' " +
           "ELSE '500만원 이상' " +
           "END " +
           "ORDER BY salaryRange")
    List<Object[]> getSalaryRangeStatistics(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 부서별 평균 급여 통계
     */
    @Query("SELECT d.name, AVG(s.netPay), COUNT(s) " +
           "FROM Salary s " +
           "JOIN s.employee e " +
           "JOIN e.department d " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID' " +
           "GROUP BY d.id, d.name " +
           "ORDER BY AVG(s.netPay) DESC")
    List<Object[]> getAverageSalaryByDepartment(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 직급별 평균 급여 통계
     */
    @Query("SELECT p.name, AVG(s.netPay), COUNT(s) " +
           "FROM Salary s " +
           "JOIN s.employee e " +
           "JOIN e.position p " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID' " +
           "GROUP BY p.id, p.name " +
           "ORDER BY AVG(s.netPay) DESC")
    List<Object[]> getAverageSalaryByPosition(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 급여 상태별 통계
     */
    @Query("SELECT s.paymentStatus, COUNT(s) " +
           "FROM Salary s " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "GROUP BY s.paymentStatus")
    List<Object[]> getSalaryCountByStatus(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 월별 급여 총액 추이 (연간)
     */
    @Query("SELECT s.payMonth, SUM(s.netPay) " +
           "FROM Salary s " +
           "WHERE s.payYear = :year AND s.paymentStatus = 'PAID' " +
           "GROUP BY s.payMonth " +
           "ORDER BY s.payMonth")
    List<Object[]> getMonthlySalaryTrend(@Param("year") Integer year);

    /**
     * 연도별 급여 총액 추이
     */
    @Query("SELECT s.payYear, SUM(s.netPay) " +
           "FROM Salary s " +
           "WHERE s.paymentStatus = 'PAID' " +
           "GROUP BY s.payYear " +
           "ORDER BY s.payYear")
    List<Object[]> getYearlySalaryTrend();

    /**
     * 초과근무 수당이 많은 직원 TOP 10
     */
    @Query("SELECT e.name, s.overtimeAllowance " +
           "FROM Salary s " +
           "JOIN s.employee e " +
           "WHERE s.payYear = :year AND s.payMonth = :month " +
           "AND s.paymentStatus = 'PAID' " +
           "ORDER BY s.overtimeAllowance DESC")
    List<Object[]> getTopOvertimeEmployees(@Param("year") Integer year, @Param("month") Integer month);
}




