package com.erp.hr.service;

import com.erp.hr.dto.EmployeeCreateDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * 직원 서비스 인터페이스
 * 직원 관련 비즈니스 로직을 정의합니다
 */
public interface EmployeeService {

    /**
     * 직원 생성
     */
    EmployeeDto createEmployee(EmployeeCreateDto createDto);

    /**
     * 직원 정보 수정
     */
    EmployeeDto updateEmployee(Long id, EmployeeUpdateDto updateDto);

    /**
     * 직원 조회 (ID)
     */
    EmployeeDto getEmployee(Long id);

    /**
     * 사번으로 직원 조회
     */
    EmployeeDto getEmployeeByNumber(String employeeNumber);

    /**
     * 이메일로 직원 조회
     */
    EmployeeDto getEmployeeByEmail(String email);

    /**
     * 전체 직원 목록 조회 (페이징)
     */
    Page<EmployeeDto> getAllEmployees(Pageable pageable);

    /**
     * 회사별 직원 목록 조회 (페이징)
     */
    Page<EmployeeDto> getEmployeesByCompany(Long companyId, Pageable pageable);

    /**
     * 부서별 직원 목록 조회 (페이징)
     */
    Page<EmployeeDto> getEmployeesByDepartment(Long departmentId, Pageable pageable);

    /**
     * 재직 중인 직원 목록 조회
     */
    List<EmployeeDto> getActiveEmployees();

    /**
     * 회사별 재직 중인 직원 목록 조회
     */
    List<EmployeeDto> getActiveEmployeesByCompany(Long companyId);

    /**
     * 직원 검색
     */
    Page<EmployeeDto> searchEmployees(String searchTerm, Pageable pageable);

    /**
     * 회사별 직원 검색
     */
    Page<EmployeeDto> searchEmployeesByCompany(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 입사일 범위로 직원 조회
     */
    List<EmployeeDto> getEmployeesByHireDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 생일인 직원 조회
     */
    List<EmployeeDto> getEmployeesByBirthday(int month, int day);

    /**
     * 이번 달 생일인 직원 조회
     */
    List<EmployeeDto> getBirthdayEmployeesThisMonth();

    /**
     * 직원 퇴직 처리
     */
    void terminateEmployee(Long id, LocalDate terminationDate, String reason);

    /**
     * 직원 복직 처리
     */
    void reactivateEmployee(Long id);

    /**
     * 직원 삭제 (소프트 삭제)
     */
    void deleteEmployee(Long id);

    /**
     * 사번 중복 확인
     */
    boolean isEmployeeNumberExists(String employeeNumber);

    /**
     * 사번 중복 확인 (본인 제외)
     */
    boolean isEmployeeNumberExists(String employeeNumber, Long excludeId);

    /**
     * 이메일 중복 확인
     */
    boolean isEmailExists(String email);

    /**
     * 이메일 중복 확인 (본인 제외)
     */
    boolean isEmailExists(String email, Long excludeId);

    /**
     * 직급별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByPosition();

    /**
     * 부서별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByDepartment();

    /**
     * 입사년도별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByHireYear();

    /**
     * 연령대별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByAgeGroup();

    /**
     * 성별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByGender();
}