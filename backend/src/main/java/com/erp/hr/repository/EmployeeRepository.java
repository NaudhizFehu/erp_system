package com.erp.hr.repository;

import com.erp.hr.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 직원 레포지토리
 * 직원 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * 사번으로 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.employeeNumber = :employeeNumber AND e.isDeleted = false")
    Optional<Employee> findByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

    /**
     * 이메일로 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.email = :email AND e.isDeleted = false")
    Optional<Employee> findByEmail(@Param("email") String email);

    /**
     * 회사별 직원 목록 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.company.id = :companyId AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 부서별 직원 목록 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.department.id = :departmentId AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 직급별 직원 목록 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.position.id = :positionId AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findByPositionId(@Param("positionId") Long positionId);

    /**
     * 근무 상태별 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.employmentStatus = :status AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findByEmploymentStatus(@Param("status") Employee.EmploymentStatus status);

    /**
     * 재직 중인 직원 목록 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findActiveEmployees();

    /**
     * 회사별 재직 중인 직원 목록 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.company.id = :companyId AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "ORDER BY e.employeeNumber")
    List<Employee> findActiveEmployeesByCompanyId(@Param("companyId") Long companyId);

    /**
     * 직원 검색 (이름, 사번, 이메일로 검색)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE (e.name LIKE %:searchTerm% OR e.employeeNumber LIKE %:searchTerm% OR e.email LIKE %:searchTerm%) " +
           "AND e.isDeleted = false")
    Page<Employee> searchEmployees(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 직원 검색
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.company.id = :companyId " +
           "AND (e.name LIKE %:searchTerm% OR e.employeeNumber LIKE %:searchTerm% OR e.email LIKE %:searchTerm%) " +
           "AND e.isDeleted = false")
    Page<Employee> searchEmployeesByCompany(@Param("companyId") Long companyId, 
                                          @Param("searchTerm") String searchTerm, 
                                          Pageable pageable);

    /**
     * 입사일 범위로 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.hireDate BETWEEN :startDate AND :endDate AND e.isDeleted = false " +
           "ORDER BY e.hireDate DESC")
    List<Employee> findByHireDateBetween(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);

    /**
     * 생일인 직원 조회 (월, 일 기준)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE MONTH(e.birthDate) = :month AND DAY(e.birthDate) = :day " +
           "AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "ORDER BY e.name")
    List<Employee> findByBirthday(@Param("month") int month, @Param("day") int day);

    /**
     * 이번 달 생일인 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE MONTH(e.birthDate) = MONTH(CURRENT_DATE) " +
           "AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "ORDER BY DAY(e.birthDate)")
    List<Employee> findBirthdayThisMonth();

    /**
     * 사번 중복 확인
     */
    boolean existsByEmployeeNumber(String employeeNumber);

    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(String email);

    /**
     * 사번 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e " +
           "WHERE e.employeeNumber = :employeeNumber AND e.id != :excludeId AND e.isDeleted = false")
    boolean existsByEmployeeNumberAndIdNot(@Param("employeeNumber") String employeeNumber, 
                                         @Param("excludeId") Long excludeId);

    /**
     * 이메일 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e " +
           "WHERE e.email = :email AND e.id != :excludeId AND e.isDeleted = false")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 전체 직원 목록 조회 (페이징, 연관관계 포함)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.isDeleted = false")
    Page<Employee> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 전체 직원 목록 조회 (페이징)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.company.id = :companyId AND e.isDeleted = false")
    Page<Employee> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 부서별 전체 직원 목록 조회 (페이징)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.department.id = :departmentId AND e.isDeleted = false")
    Page<Employee> findByDepartmentIdWithDetails(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * 근속년수별 직원 조회
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE YEAR(CURRENT_DATE) - YEAR(e.hireDate) = :years " +
           "AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "ORDER BY e.hireDate")
    List<Employee> findByYearsOfService(@Param("years") int years);

    /**
     * 퇴직 예정 직원 조회 (퇴사일이 설정된 직원)
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.terminationDate IS NOT NULL " +
           "AND e.terminationDate BETWEEN :startDate AND :endDate " +
           "AND e.isDeleted = false " +
           "ORDER BY e.terminationDate")
    List<Employee> findByTerminationDateBetween(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);

    /**
     * 직급별 직원 수 통계
     */
    @Query("SELECT p.name, COUNT(e) FROM Employee e " +
           "JOIN e.position p " +
           "WHERE e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "GROUP BY p.id, p.name " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByPosition();

    /**
     * 부서별 직원 수 통계
     */
    @Query("SELECT d.name, COUNT(e) FROM Employee e " +
           "JOIN e.department d " +
           "WHERE e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "GROUP BY d.id, d.name " +
           "ORDER BY COUNT(e) DESC")
    List<Object[]> getEmployeeCountByDepartment();

    /**
     * 입사년도별 직원 수 통계
     */
    @Query("SELECT YEAR(e.hireDate), COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false " +
           "GROUP BY YEAR(e.hireDate) " +
           "ORDER BY YEAR(e.hireDate) DESC")
    List<Object[]> getEmployeeCountByHireYear();

    /**
     * 연령대별 직원 수 통계
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 30 THEN '20대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 40 THEN '30대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 50 THEN '40대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 60 THEN '50대' " +
           "ELSE '60대 이상' " +
           "END as ageGroup, COUNT(e) " +
           "FROM Employee e " +
           "WHERE e.birthDate IS NOT NULL AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "GROUP BY " +
           "CASE " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 30 THEN '20대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 40 THEN '30대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 50 THEN '40대' " +
           "WHEN YEAR(CURRENT_DATE) - YEAR(e.birthDate) < 60 THEN '50대' " +
           "ELSE '60대 이상' " +
           "END " +
           "ORDER BY ageGroup")
    List<Object[]> getEmployeeCountByAgeGroup();

    /**
     * 성별 직원 수 통계
     */
    @Query("SELECT e.gender, COUNT(e) FROM Employee e " +
           "WHERE e.gender IS NOT NULL AND e.employmentStatus = 'ACTIVE' AND e.isDeleted = false " +
           "GROUP BY e.gender")
    List<Object[]> getEmployeeCountByGender();

    // ==================== Dashboard용 메서드들 ====================
    
    /**
     * 회사별 삭제되지 않은 직원 수
     */
    long countByCompanyIdAndIsDeletedFalse(Long companyId);
    
    /**
     * 활성 직원 수
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company.id = :companyId " +
           "AND e.isDeleted = false AND e.employmentStatus = 'ACTIVE'")
    long countActiveEmployees(@Param("companyId") Long companyId);
    
    /**
     * 기간별 신규 직원 수
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company.id = :companyId " +
           "AND e.isDeleted = false AND e.hireDate BETWEEN :startDate AND :endDate")
    long countNewEmployees(@Param("companyId") Long companyId, 
                          @Param("startDate") LocalDate startDate, 
                          @Param("endDate") LocalDate endDate);
    
    /**
     * 회사별 부서별 직원 수
     */
    @Query("SELECT d.name, COUNT(e) FROM Employee e JOIN e.department d " +
           "WHERE e.company.id = :companyId AND e.isDeleted = false " +
           "GROUP BY d.id, d.name")
    List<Object[]> getEmployeeCountByDepartment(@Param("companyId") Long companyId);

    /**
     * 전역 검색용 - 회사별 직원명으로 검색
     */
    @Query("SELECT e FROM Employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.company.id = :companyId AND e.name LIKE %:name% AND e.isDeleted = false")
    List<Employee> findByCompanyIdAndNameContainingIgnoreCase(@Param("companyId") Long companyId, @Param("name") String name);
}
