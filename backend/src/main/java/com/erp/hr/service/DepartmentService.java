package com.erp.hr.service;

import com.erp.common.dto.DepartmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 부서 서비스 인터페이스
 * 부서 관련 비즈니스 로직을 정의합니다
 */
public interface DepartmentService {

    /**
     * 부서 생성
     */
    DepartmentDto createDepartment(DepartmentDto.DepartmentCreateDto createDto);

    /**
     * 부서 정보 수정
     */
    DepartmentDto updateDepartment(Long id, DepartmentDto.DepartmentUpdateDto updateDto);

    /**
     * 부서 조회 (ID)
     */
    DepartmentDto getDepartment(Long id);

    /**
     * 부서 코드로 조회
     */
    DepartmentDto getDepartmentByCode(String departmentCode);

    /**
     * 전체 부서 목록 조회 (페이징)
     */
    Page<DepartmentDto> getAllDepartments(Pageable pageable);

    /**
     * 회사별 부서 목록 조회 (페이징)
     */
    Page<DepartmentDto> getDepartmentsByCompany(Long companyId, Pageable pageable);

    /**
     * 상위 부서별 하위 부서 목록 조회
     */
    List<DepartmentDto> getSubDepartments(Long parentDepartmentId);

    /**
     * 최상위 부서 목록 조회
     */
    List<DepartmentDto> getRootDepartments();

    /**
     * 회사별 최상위 부서 목록 조회
     */
    List<DepartmentDto> getRootDepartmentsByCompany(Long companyId);

    /**
     * 활성 부서 목록 조회
     */
    List<DepartmentDto> getActiveDepartments();

    /**
     * 회사별 활성 부서 목록 조회
     */
    List<DepartmentDto> getActiveDepartmentsByCompany(Long companyId);

    /**
     * 부서 검색
     */
    Page<DepartmentDto> searchDepartments(String searchTerm, Pageable pageable);

    /**
     * 회사별 부서 검색
     */
    Page<DepartmentDto> searchDepartmentsByCompany(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 부서 삭제 (소프트 삭제)
     */
    void deleteDepartment(Long id);

    /**
     * 부서 코드 중복 확인
     */
    boolean isDepartmentCodeExists(String departmentCode);

    /**
     * 부서 코드 중복 확인 (본인 제외)
     */
    boolean isDepartmentCodeExists(String departmentCode, Long excludeId);

    /**
     * 부서별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByDepartment();

    /**
     * 회사별 부서별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByDepartment(Long companyId);

    /**
     * 부서 계층 구조 조회
     */
    List<DepartmentDto> getDepartmentHierarchy(Long companyId);
}
