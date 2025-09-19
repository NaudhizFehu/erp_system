package com.erp.hr.repository;

import com.erp.hr.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 부서 레포지토리
 * 부서 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 부서 코드로 부서 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.departmentCode = :departmentCode AND d.isDeleted = false")
    Optional<Department> findByDepartmentCode(@Param("departmentCode") String departmentCode);

    /**
     * 회사별 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 회사별 부서 목록 조회 (페이징)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.isDeleted = false ORDER BY d.sortOrder")
    Page<Department> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 상위 부서로 하위 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.parentDepartment.id = :parentId AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findByParentDepartmentId(@Param("parentId") Long parentId);

    /**
     * 최상위 부서 목록 조회 (상위 부서가 없는 부서들)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.parentDepartment IS NULL AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findRootDepartments();

    /**
     * 회사별 최상위 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.parentDepartment IS NULL AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findRootDepartmentsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 부서명으로 검색
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.name LIKE %:name% AND d.isDeleted = false")
    List<Department> findByNameContaining(@Param("name") String name);

    /**
     * 회사별 부서명 검색
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.name LIKE %:name% AND d.isDeleted = false")
    List<Department> findByCompanyIdAndNameContaining(@Param("companyId") Long companyId, @Param("name") String name);

    /**
     * 활성 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.status = 'ACTIVE' AND d.isDeleted = false ORDER BY d.company.name, d.sortOrder")
    List<Department> findActiveDepartments();

    /**
     * 회사별 활성 부서 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.status = 'ACTIVE' AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findActiveDepartmentsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 상태별 부서 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.status = :status AND d.isDeleted = false")
    List<Department> findByStatus(@Param("status") Department.DepartmentStatus status);

    /**
     * 관리자가 있는 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c JOIN FETCH d.manager m WHERE d.manager IS NOT NULL AND d.isDeleted = false")
    List<Department> findDepartmentsWithManager();

    /**
     * 관리자가 없는 부서 목록 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c WHERE d.manager IS NULL AND d.isDeleted = false")
    List<Department> findDepartmentsWithoutManager();

    /**
     * 부서 코드 중복 확인
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d WHERE d.departmentCode = :departmentCode AND d.isDeleted = false")
    boolean existsByDepartmentCode(@Param("departmentCode") String departmentCode);

    /**
     * 부서 코드 중복 확인 (삭제되지 않은 것만)
     */
    boolean existsByDepartmentCodeAndIsDeletedFalse(String departmentCode);

    /**
     * 부서 코드 중복 확인 (본인 제외, 삭제되지 않은 것만)
     */
    boolean existsByDepartmentCodeAndIdNotAndIsDeletedFalse(String departmentCode, Long excludeId);

    /**
     * 회사별 부서 코드 중복 확인
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d WHERE d.company.id = :companyId AND d.departmentCode = :departmentCode AND d.isDeleted = false")
    boolean existsByCompanyIdAndDepartmentCode(@Param("companyId") Long companyId, @Param("departmentCode") String departmentCode);

    /**
     * 부서 코드 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d WHERE d.departmentCode = :departmentCode AND d.id != :excludeId AND d.isDeleted = false")
    boolean existsByDepartmentCodeAndIdNot(@Param("departmentCode") String departmentCode, @Param("excludeId") Long excludeId);

    /**
     * 전체 부서 목록 조회 (페이징, 연관관계 포함)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.isDeleted = false")
    Page<Department> findAllWithCompanyAndManager(Pageable pageable);

    /**
     * 회사별 부서 검색 (페이징)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.name LIKE %:searchTerm% AND d.isDeleted = false")
    Page<Department> searchByCompanyAndTerm(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 부서 검색 (페이징)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.name LIKE %:searchTerm% AND d.isDeleted = false")
    Page<Department> searchDepartments(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 부서 검색 (페이징)
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.name LIKE %:searchTerm% AND d.isDeleted = false")
    Page<Department> searchDepartmentsByCompany(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 부서 계층 구조 조회 (특정 부서의 모든 하위 부서)
     */
    @Query(value = "WITH RECURSIVE dept_hierarchy AS (" +
                  "  SELECT id, department_code, name, parent_department_id, company_id " +
                  "  FROM departments " +
                  "  WHERE id = :departmentId AND is_deleted = false " +
                  "  UNION ALL " +
                  "  SELECT d.id, d.department_code, d.name, d.parent_department_id, d.company_id " +
                  "  FROM departments d " +
                  "  INNER JOIN dept_hierarchy dh ON d.parent_department_id = dh.id " +
                  "  WHERE d.is_deleted = false " +
                  ") " +
                  "SELECT id FROM dept_hierarchy", 
           nativeQuery = true)
    List<Long> findDepartmentHierarchyIds(@Param("departmentId") Long departmentId);

    /**
     * 부서 통계 조회 - 회사별 부서 수
     */
    @Query("SELECT d.company.name, COUNT(d) FROM Department d WHERE d.isDeleted = false GROUP BY d.company.id, d.company.name")
    List<Object[]> getDepartmentCountByCompany();

    /**
     * 부서 통계 조회 - 상태별 부서 수
     */
    @Query("SELECT d.status, COUNT(d) FROM Department d WHERE d.isDeleted = false GROUP BY d.status")
    List<Object[]> getDepartmentCountByStatus();

    /**
     * 전역 검색용 - 회사별 부서명으로 검색
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND d.isDeleted = false")
    List<Department> findByCompanyIdAndNameContainingIgnoreCase(@Param("companyId") Long companyId, @Param("name") String name);

    /**
     * 부서별 직원 수 통계
     */
    @Query("SELECT d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e WHERE d.isDeleted = false AND (e IS NULL OR e.isDeleted = false) GROUP BY d.id, d.name")
    List<Object[]> getEmployeeCountByDepartment();

    /**
     * 회사별 부서별 직원 수 통계
     */
    @Query("SELECT d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e WHERE d.company.id = :companyId AND d.isDeleted = false AND (e IS NULL OR e.isDeleted = false) GROUP BY d.id, d.name")
    List<Object[]> getEmployeeCountByDepartment(@Param("companyId") Long companyId);

    /**
     * 부서 계층 구조 조회
     */
    @Query("SELECT d FROM Department d JOIN FETCH d.company c LEFT JOIN FETCH d.manager m WHERE d.company.id = :companyId AND d.isDeleted = false ORDER BY d.sortOrder")
    List<Department> findDepartmentHierarchy(@Param("companyId") Long companyId);
}
