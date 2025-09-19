package com.erp.hr.service.impl;

import com.erp.common.dto.DepartmentDto;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.entity.Department;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.hr.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 부서 서비스 구현체
 * 부서 관련 비즈니스 로직을 구현합니다
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public DepartmentDto createDepartment(DepartmentDto.DepartmentCreateDto createDto) {
        log.info("부서 생성: {}", createDto.name());

        // 부서 코드 중복 확인
        if (isDepartmentCodeExists(createDto.departmentCode())) {
            throw new IllegalArgumentException("이미 존재하는 부서 코드입니다: " + createDto.departmentCode());
        }

        // 회사 조회
        Company company = companyRepository.findById(createDto.companyId())
                .orElseThrow(() -> ExceptionUtils.entityNotFound("회사를 찾을 수 없습니다"));

        // 부서 엔티티 생성
        Department department = new Department();
        department.setDepartmentCode(createDto.departmentCode());
        department.setName(createDto.name());
        department.setNameEn(createDto.nameEn());
        department.setDescription(createDto.description());
        department.setCompany(company);
        department.setLevel(createDto.level() != null ? createDto.level() : 1);
        department.setSortOrder(createDto.sortOrder() != null ? createDto.sortOrder() : 0);
        department.setDepartmentType(createDto.departmentType() != null ? createDto.departmentType() : Department.DepartmentType.DEPARTMENT);
        department.setStatus(createDto.status() != null ? createDto.status() : Department.DepartmentStatus.ACTIVE);

        // 상위 부서 설정
        if (createDto.parentDepartmentId() != null) {
            Department parentDepartment = departmentRepository.findById(createDto.parentDepartmentId())
                    .orElseThrow(() -> ExceptionUtils.entityNotFound("상위 부서를 찾을 수 없습니다"));
            department.setParentDepartment(parentDepartment);
        }

        Department savedDepartment = departmentRepository.save(department);
        log.info("부서 생성 완료: ID {}", savedDepartment.getId());

        return DepartmentDto.from(savedDepartment);
    }

    @Override
    public DepartmentDto updateDepartment(Long id, DepartmentDto.DepartmentUpdateDto updateDto) {
        log.info("부서 수정: ID {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));

        // 부서 코드 중복 확인 (본인 제외)
        if (updateDto.departmentCode() != null && 
            isDepartmentCodeExists(updateDto.departmentCode(), id)) {
            throw new IllegalArgumentException("이미 존재하는 부서 코드입니다: " + updateDto.departmentCode());
        }

        // 필드 업데이트
        if (updateDto.departmentCode() != null) {
            department.setDepartmentCode(updateDto.departmentCode());
        }
        if (updateDto.name() != null) {
            department.setName(updateDto.name());
        }
        if (updateDto.nameEn() != null) {
            department.setNameEn(updateDto.nameEn());
        }
        if (updateDto.description() != null) {
            department.setDescription(updateDto.description());
        }
        if (updateDto.level() != null) {
            department.setLevel(updateDto.level());
        }
        if (updateDto.sortOrder() != null) {
            department.setSortOrder(updateDto.sortOrder());
        }
        if (updateDto.departmentType() != null) {
            department.setDepartmentType(updateDto.departmentType());
        }
        if (updateDto.status() != null) {
            department.setStatus(updateDto.status());
        }

        // 상위 부서 업데이트
        if (updateDto.parentDepartmentId() != null) {
            if (updateDto.parentDepartmentId() == 0) {
                department.setParentDepartment(null);
            } else {
                Department parentDepartment = departmentRepository.findById(updateDto.parentDepartmentId())
                        .orElseThrow(() -> ExceptionUtils.entityNotFound("상위 부서를 찾을 수 없습니다"));
                department.setParentDepartment(parentDepartment);
            }
        }

        Department updatedDepartment = departmentRepository.save(department);
        log.info("부서 수정 완료: ID {}", updatedDepartment.getId());

        return DepartmentDto.from(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto getDepartment(Long id) {
        log.info("부서 조회: ID {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));

        return DepartmentDto.from(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentByCode(String departmentCode) {
        log.info("부서 코드로 조회: {}", departmentCode);

        Department department = departmentRepository.findByDepartmentCode(departmentCode)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));

        return DepartmentDto.from(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> getAllDepartments(Pageable pageable) {
        log.info("전체 부서 목록 조회");

        Page<Department> departments = departmentRepository.findAll(pageable);
        return departments.map(DepartmentDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> getDepartmentsByCompany(Long companyId, Pageable pageable) {
        log.info("회사별 부서 목록 조회: 회사 ID {}", companyId);

        Page<Department> departments = departmentRepository.findByCompanyId(companyId, pageable);
        return departments.map(DepartmentDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getSubDepartments(Long parentDepartmentId) {
        log.info("하위 부서 목록 조회: 상위 부서 ID {}", parentDepartmentId);

        List<Department> departments = departmentRepository.findByParentDepartmentId(parentDepartmentId);
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getRootDepartments() {
        log.info("최상위 부서 목록 조회");

        List<Department> departments = departmentRepository.findRootDepartments();
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getRootDepartmentsByCompany(Long companyId) {
        log.info("회사별 최상위 부서 목록 조회: 회사 ID {}", companyId);

        List<Department> departments = departmentRepository.findRootDepartmentsByCompanyId(companyId);
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getActiveDepartments() {
        log.info("활성 부서 목록 조회");

        List<Department> departments = departmentRepository.findActiveDepartments();
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getActiveDepartmentsByCompany(Long companyId) {
        log.info("회사별 활성 부서 목록 조회: 회사 ID {}", companyId);

        List<Department> departments = departmentRepository.findActiveDepartmentsByCompanyId(companyId);
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> searchDepartments(String searchTerm, Pageable pageable) {
        log.info("부서 검색: {}", searchTerm);

        Page<Department> departments = departmentRepository.searchDepartments(searchTerm, pageable);
        return departments.map(DepartmentDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDto> searchDepartmentsByCompany(Long companyId, String searchTerm, Pageable pageable) {
        log.info("회사별 부서 검색: 회사 ID {}, 검색어 {}", companyId, searchTerm);

        Page<Department> departments = departmentRepository.searchDepartmentsByCompany(companyId, searchTerm, pageable);
        return departments.map(DepartmentDto::from);
    }

    @Override
    public void deleteDepartment(Long id) {
        log.info("부서 삭제: ID {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));

        department.setIsDeleted(true);
        departmentRepository.save(department);
        log.info("부서 삭제 완료: ID {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDepartmentCodeExists(String departmentCode) {
        return departmentRepository.existsByDepartmentCodeAndIsDeletedFalse(departmentCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDepartmentCodeExists(String departmentCode, Long excludeId) {
        return departmentRepository.existsByDepartmentCodeAndIdNotAndIsDeletedFalse(departmentCode, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getEmployeeCountByDepartment() {
        log.info("부서별 직원 수 통계 조회");

        return departmentRepository.getEmployeeCountByDepartment();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getEmployeeCountByDepartment(Long companyId) {
        log.info("회사별 부서별 직원 수 통계 조회: 회사 ID {}", companyId);

        return departmentRepository.getEmployeeCountByDepartment(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentHierarchy(Long companyId) {
        log.info("부서 계층 구조 조회: 회사 ID {}", companyId);

        List<Department> departments = departmentRepository.findDepartmentHierarchy(companyId);
        return departments.stream().map(DepartmentDto::from).collect(Collectors.toList());
    }
}
