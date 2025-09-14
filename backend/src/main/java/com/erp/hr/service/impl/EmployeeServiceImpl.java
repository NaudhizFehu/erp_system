package com.erp.hr.service.impl;

import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.entity.Department;
import com.erp.common.repository.DepartmentRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.dto.EmployeeCreateDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import com.erp.hr.entity.Employee;
import com.erp.hr.entity.Position;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.hr.repository.PositionRepository;
import com.erp.hr.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 직원 서비스 구현체
 * 직원 관련 비즈니스 로직을 구현합니다
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateDto createDto) {
        log.info("직원 생성 시작: {}", createDto.employeeNumber());

        // 중복 검증
        if (employeeRepository.existsByEmployeeNumber(createDto.employeeNumber())) {
            ExceptionUtils.throwDuplicate("이미 존재하는 사번입니다: " + createDto.employeeNumber());
        }

        if (employeeRepository.existsByEmail(createDto.email())) {
            ExceptionUtils.throwDuplicate("이미 존재하는 이메일입니다: " + createDto.email());
        }

        // 연관 엔티티 조회
        Company company = companyRepository.findById(createDto.companyId())
                .orElseThrow(() -> ExceptionUtils.entityNotFound("회사를 찾을 수 없습니다"));

        Department department = departmentRepository.findById(createDto.departmentId())
                .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));

        Position position = positionRepository.findById(createDto.positionId())
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직급을 찾을 수 없습니다"));

        // 급여 범위 검증
        if (createDto.baseSalary() != null && !position.isValidSalaryRange(createDto.baseSalary())) {
            ExceptionUtils.throwValidation("기본급이 해당 직급의 급여 범위를 벗어납니다");
        }

        // Employee 엔티티 생성
        Employee employee = new Employee();
        employee.setEmployeeNumber(createDto.employeeNumber());
        employee.setName(createDto.name());
        employee.setNameEn(createDto.nameEn());
        employee.setEmail(createDto.email());
        employee.setPhone(createDto.phone());
        employee.setMobile(createDto.mobile());
        employee.setResidentNumber(createDto.residentNumber());
        employee.setBirthDate(createDto.birthDate());
        employee.setGender(createDto.gender());
        employee.setAddress(createDto.address());
        employee.setAddressDetail(createDto.addressDetail());
        employee.setPostalCode(createDto.postalCode());
        employee.setCompany(company);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setHireDate(createDto.hireDate());
        employee.setEmploymentStatus(createDto.employmentStatus());
        employee.setEmploymentType(createDto.employmentType());
        employee.setBaseSalary(createDto.baseSalary());
        employee.setBankName(createDto.bankName());
        employee.setAccountNumber(createDto.accountNumber());
        employee.setAccountHolder(createDto.accountHolder());
        employee.setEmergencyContact(createDto.emergencyContact());
        employee.setEmergencyRelation(createDto.emergencyRelation());
        employee.setEducation(createDto.education());
        employee.setMajor(createDto.major());
        employee.setCareer(createDto.career());
        employee.setSkills(createDto.skills());
        employee.setCertifications(createDto.certifications());
        employee.setMemo(createDto.memo());
        employee.setProfileImageUrl(createDto.profileImageUrl());

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("직원 생성 완료: ID {}, 사번 {}", savedEmployee.getId(), savedEmployee.getEmployeeNumber());

        return EmployeeDto.from(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeUpdateDto updateDto) {
        log.info("직원 수정 시작: ID {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        // 이메일 중복 검증 (본인 제외)
        if (updateDto.email() != null && 
            employeeRepository.existsByEmailAndIdNot(updateDto.email(), id)) {
            ExceptionUtils.throwDuplicate("이미 존재하는 이메일입니다: " + updateDto.email());
        }

        // 연관 엔티티 업데이트
        if (updateDto.departmentId() != null) {
            Department department = departmentRepository.findById(updateDto.departmentId())
                    .orElseThrow(() -> ExceptionUtils.entityNotFound("부서를 찾을 수 없습니다"));
            employee.setDepartment(department);
        }

        if (updateDto.positionId() != null) {
            Position position = positionRepository.findById(updateDto.positionId())
                    .orElseThrow(() -> ExceptionUtils.entityNotFound("직급을 찾을 수 없습니다"));
            
            // 급여 범위 검증
            if (updateDto.baseSalary() != null && !position.isValidSalaryRange(updateDto.baseSalary())) {
                ExceptionUtils.throwValidation("기본급이 해당 직급의 급여 범위를 벗어납니다");
            }
            
            employee.setPosition(position);
        }

        // 필드 업데이트 (null이 아닌 경우에만)
        if (updateDto.name() != null) employee.setName(updateDto.name());
        if (updateDto.nameEn() != null) employee.setNameEn(updateDto.nameEn());
        if (updateDto.email() != null) employee.setEmail(updateDto.email());
        if (updateDto.phone() != null) employee.setPhone(updateDto.phone());
        if (updateDto.mobile() != null) employee.setMobile(updateDto.mobile());
        if (updateDto.birthDate() != null) employee.setBirthDate(updateDto.birthDate());
        if (updateDto.gender() != null) employee.setGender(updateDto.gender());
        if (updateDto.address() != null) employee.setAddress(updateDto.address());
        if (updateDto.addressDetail() != null) employee.setAddressDetail(updateDto.addressDetail());
        if (updateDto.postalCode() != null) employee.setPostalCode(updateDto.postalCode());
        if (updateDto.employmentStatus() != null) employee.setEmploymentStatus(updateDto.employmentStatus());
        if (updateDto.employmentType() != null) employee.setEmploymentType(updateDto.employmentType());
        if (updateDto.baseSalary() != null) employee.setBaseSalary(updateDto.baseSalary());
        if (updateDto.bankName() != null) employee.setBankName(updateDto.bankName());
        if (updateDto.accountNumber() != null) employee.setAccountNumber(updateDto.accountNumber());
        if (updateDto.accountHolder() != null) employee.setAccountHolder(updateDto.accountHolder());
        if (updateDto.emergencyContact() != null) employee.setEmergencyContact(updateDto.emergencyContact());
        if (updateDto.emergencyRelation() != null) employee.setEmergencyRelation(updateDto.emergencyRelation());
        if (updateDto.education() != null) employee.setEducation(updateDto.education());
        if (updateDto.major() != null) employee.setMajor(updateDto.major());
        if (updateDto.career() != null) employee.setCareer(updateDto.career());
        if (updateDto.skills() != null) employee.setSkills(updateDto.skills());
        if (updateDto.certifications() != null) employee.setCertifications(updateDto.certifications());
        if (updateDto.memo() != null) employee.setMemo(updateDto.memo());
        if (updateDto.profileImageUrl() != null) employee.setProfileImageUrl(updateDto.profileImageUrl());

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("직원 수정 완료: ID {}", savedEmployee.getId());

        return EmployeeDto.from(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployee(Long id) {
        log.info("직원 조회: ID {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        return EmployeeDto.from(employee);
    }

    @Override
    public EmployeeDto getEmployeeByNumber(String employeeNumber) {
        log.info("사번으로 직원 조회: {}", employeeNumber);

        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        return EmployeeDto.from(employee);
    }

    @Override
    public EmployeeDto getEmployeeByEmail(String email) {
        log.info("이메일로 직원 조회: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        return EmployeeDto.from(employee);
    }

    @Override
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        log.info("전체 직원 목록 조회: 페이지 {}", pageable.getPageNumber());

        Page<Employee> employees = employeeRepository.findAllWithDetails(pageable);
        return employees.map(EmployeeDto::from);
    }

    @Override
    public Page<EmployeeDto> getEmployeesByCompany(Long companyId, Pageable pageable) {
        log.info("회사별 직원 목록 조회: 회사 ID {}", companyId);

        Page<Employee> employees = employeeRepository.findByCompanyIdWithDetails(companyId, pageable);
        return employees.map(EmployeeDto::from);
    }

    @Override
    public Page<EmployeeDto> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        log.info("부서별 직원 목록 조회: 부서 ID {}", departmentId);

        Page<Employee> employees = employeeRepository.findByDepartmentIdWithDetails(departmentId, pageable);
        return employees.map(EmployeeDto::from);
    }

    @Override
    public List<EmployeeDto> getActiveEmployees() {
        log.info("재직 중인 직원 목록 조회");

        List<Employee> employees = employeeRepository.findActiveEmployees();
        return employees.stream().map(EmployeeDto::from).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getActiveEmployeesByCompany(Long companyId) {
        log.info("회사별 재직 중인 직원 목록 조회: 회사 ID {}", companyId);

        List<Employee> employees = employeeRepository.findActiveEmployeesByCompanyId(companyId);
        return employees.stream().map(EmployeeDto::from).collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDto> searchEmployees(String searchTerm, Pageable pageable) {
        log.info("직원 검색: {}", searchTerm);

        Page<Employee> employees = employeeRepository.searchEmployees(searchTerm, pageable);
        return employees.map(EmployeeDto::from);
    }

    @Override
    public Page<EmployeeDto> searchEmployeesByCompany(Long companyId, String searchTerm, Pageable pageable) {
        log.info("회사별 직원 검색: 회사 ID {}, 검색어 {}", companyId, searchTerm);

        Page<Employee> employees = employeeRepository.searchEmployeesByCompany(companyId, searchTerm, pageable);
        return employees.map(EmployeeDto::from);
    }

    @Override
    public List<EmployeeDto> getEmployeesByHireDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("입사일 범위 직원 조회: {} ~ {}", startDate, endDate);

        List<Employee> employees = employeeRepository.findByHireDateBetween(startDate, endDate);
        return employees.stream().map(EmployeeDto::from).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByBirthday(int month, int day) {
        log.info("생일 직원 조회: {}월 {}일", month, day);

        List<Employee> employees = employeeRepository.findByBirthday(month, day);
        return employees.stream().map(EmployeeDto::from).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getBirthdayEmployeesThisMonth() {
        log.info("이번 달 생일 직원 조회");

        List<Employee> employees = employeeRepository.findBirthdayThisMonth();
        return employees.stream().map(EmployeeDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void terminateEmployee(Long id, LocalDate terminationDate, String reason) {
        log.info("직원 퇴직 처리: ID {}, 퇴사일 {}", id, terminationDate);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        employee.terminate(terminationDate, reason);
        employeeRepository.save(employee);

        log.info("직원 퇴직 처리 완료: ID {}", id);
    }

    @Override
    @Transactional
    public void reactivateEmployee(Long id) {
        log.info("직원 복직 처리: ID {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        employee.reactivate();
        employeeRepository.save(employee);

        log.info("직원 복직 처리 완료: ID {}", id);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("직원 삭제: ID {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("직원을 찾을 수 없습니다"));

        employee.softDelete(null); // BaseEntity의 소프트 삭제
        employeeRepository.save(employee);

        log.info("직원 삭제 완료: ID {}", id);
    }

    @Override
    public boolean isEmployeeNumberExists(String employeeNumber) {
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }

    @Override
    public boolean isEmployeeNumberExists(String employeeNumber, Long excludeId) {
        return employeeRepository.existsByEmployeeNumberAndIdNot(employeeNumber, excludeId);
    }

    @Override
    public boolean isEmailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public boolean isEmailExists(String email, Long excludeId) {
        return employeeRepository.existsByEmailAndIdNot(email, excludeId);
    }

    @Override
    public List<Object[]> getEmployeeCountByPosition() {
        log.info("직급별 직원 수 통계 조회");
        return employeeRepository.getEmployeeCountByPosition();
    }

    @Override
    public List<Object[]> getEmployeeCountByDepartment() {
        log.info("부서별 직원 수 통계 조회");
        return employeeRepository.getEmployeeCountByDepartment();
    }

    @Override
    public List<Object[]> getEmployeeCountByHireYear() {
        log.info("입사년도별 직원 수 통계 조회");
        return employeeRepository.getEmployeeCountByHireYear();
    }

    @Override
    public List<Object[]> getEmployeeCountByAgeGroup() {
        log.info("연령대별 직원 수 통계 조회");
        return employeeRepository.getEmployeeCountByAgeGroup();
    }

    @Override
    public List<Object[]> getEmployeeCountByGender() {
        log.info("성별 직원 수 통계 조회");
        return employeeRepository.getEmployeeCountByGender();
    }
}
