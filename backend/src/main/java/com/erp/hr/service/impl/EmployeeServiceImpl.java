package com.erp.hr.service.impl;

import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.hr.entity.Department;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.dto.EmployeeCreateDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import com.erp.hr.dto.ImportResult;
import com.erp.hr.entity.Employee;
import com.erp.hr.entity.Position;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.hr.repository.PositionRepository;
import com.erp.hr.service.EmployeeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 직원 서비스 구현체
 * 직원 관련 비즈니스 로직을 구현합니다
 */
@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

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

        // 급여 범위 검증 - baseSalary 필드 제거로 인해 비활성화

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
            
            // 급여 범위 검증 - baseSalary 필드 제거로 인해 비활성화
            
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

        Employee employee = employeeRepository.findByIdWithDetails(id)
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
    public Page<EmployeeDto> getAllEmployees(Pageable pageable, Employee.EmploymentStatus employmentStatus) {
        log.info("전체 직원 목록 조회: 페이지 {}, 상태 {}", pageable.getPageNumber(), employmentStatus);

        Page<Employee> employees;
        if (employmentStatus != null) {
            employees = employeeRepository.findAllWithDetailsByStatus(employmentStatus, pageable);
        } else {
            employees = employeeRepository.findAllWithDetails(pageable);
        }
        return employees.map(EmployeeDto::from);
    }

    @Override
    public Page<EmployeeDto> getEmployeesByCompany(Long companyId, Pageable pageable, Employee.EmploymentStatus employmentStatus) {
        log.info("회사별 직원 목록 조회: 회사 ID {}, 상태 {}", companyId, employmentStatus);

        Page<Employee> employees;
        if (employmentStatus != null) {
            employees = employeeRepository.findByCompanyIdWithDetailsByStatus(companyId, employmentStatus, pageable);
        } else {
            employees = employeeRepository.findByCompanyIdWithDetails(companyId, pageable);
        }
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

    @Override
    public List<EmployeeDto> getRecentEmployeesByCompany(Long companyId) {
        log.info("회사별 최근 직원 목록 조회: 회사 ID {}", companyId);
        
        Pageable pageable = PageRequest.of(0, 5);
        List<Employee> employees = employeeRepository.findTop5ByCompanyIdOrderByEmployeeNumberDesc(companyId, pageable);
        
        return employees.stream()
                .map(employee -> EmployeeDto.from(employee))
                .collect(Collectors.toList());
    }

    @Override
    public ByteArrayResource exportToExcel(Long companyId) {
        log.info("엑셀 내보내기: 회사 ID {}", companyId);
        
        try {
            List<Employee> employees;
            if (companyId == null) {
                employees = employeeRepository.findAll();
            } else {
                employees = employeeRepository.findByCompanyId(companyId);
            }
            
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("직원 목록");
            
            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"사번", "이름", "이메일", "전화번호", "회사", "부서", "직급", "입사일", "재직상태"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 데이터 행 생성
            int rowNum = 1;
            for (Employee employee : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(employee.getEmployeeNumber() != null ? employee.getEmployeeNumber() : "");
                row.createCell(1).setCellValue(employee.getName() != null ? employee.getName() : "");
                row.createCell(2).setCellValue(employee.getEmail() != null ? employee.getEmail() : "");
                row.createCell(3).setCellValue(employee.getPhone() != null ? employee.getPhone() : "");
                row.createCell(4).setCellValue(employee.getCompany() != null ? employee.getCompany().getName() : "");
                row.createCell(5).setCellValue(employee.getDepartment() != null ? employee.getDepartment().getName() : "");
                row.createCell(6).setCellValue(employee.getPosition() != null ? employee.getPosition().getName() : "");
                row.createCell(7).setCellValue(employee.getHireDate() != null ? employee.getHireDate().toString() : "");
                row.createCell(8).setCellValue(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().toString() : "");
            }
            
            // 파일로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("엑셀 내보내기 실패", e);
            throw new RuntimeException("엑셀 내보내기 실패", e);
        }
    }

    @Override
    public ByteArrayResource exportToCsv(Long companyId) {
        log.info("CSV 내보내기: 회사 ID {}", companyId);
        
        try {
            List<Employee> employees;
            if (companyId == null) {
                employees = employeeRepository.findAll();
            } else {
                employees = employeeRepository.findByCompanyId(companyId);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            // UTF-8 BOM 추가 (Excel 한글 호환)
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                 CSVWriter csvWriter = new CSVWriter(writer)) {
                
                // 헤더 작성
                String[] headers = {"사번", "이름", "이메일", "전화번호", "회사", "부서", "직급", "입사일", "재직상태"};
                csvWriter.writeNext(headers);
                
                // 데이터 작성
                for (Employee employee : employees) {
                    String[] row = {
                        employee.getEmployeeNumber() != null ? employee.getEmployeeNumber() : "",
                        employee.getName() != null ? employee.getName() : "",
                        employee.getEmail() != null ? employee.getEmail() : "",
                        employee.getPhone() != null ? employee.getPhone() : "",
                        employee.getCompany() != null ? employee.getCompany().getName() : "",
                        employee.getDepartment() != null ? employee.getDepartment().getName() : "",
                        employee.getPosition() != null ? employee.getPosition().getName() : "",
                        employee.getHireDate() != null ? employee.getHireDate().toString() : "",
                        employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().toString() : ""
                    };
                    csvWriter.writeNext(row);
                }
            }
            
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("CSV 내보내기 실패", e);
            throw new RuntimeException("CSV 내보내기 실패", e);
        }
    }

    @Override
    @Transactional
    public ImportResult importFromExcel(MultipartFile file, Long companyId) {
        log.info("엑셀 가져오기: 회사 ID {}, 파일명 {}", companyId, file.getOriginalFilename());
        
        if (companyId == null) {
            throw new IllegalArgumentException("가져오기 시 회사 ID는 필수입니다");
        }
        
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 첫 행은 헤더로 스킵
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                totalRows++;
                
                try {
                    String employeeNumber = getCellValueAsString(row.getCell(0));
                    String name = getCellValueAsString(row.getCell(1));
                    String email = getCellValueAsString(row.getCell(2));
                    
                    if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
                        errors.add(String.format("%d행: 사번이 없습니다", i + 1));
                        continue;
                    }
                    
                    // 중복 체크
                    if (employeeRepository.existsByEmployeeNumber(employeeNumber)) {
                        errors.add(String.format("%d행: 사번 %s가 이미 존재합니다", i + 1, employeeNumber));
                        continue;
                    }
                    
                    // 간단한 직원 생성 (실제로는 더 많은 필드가 필요)
                    Company company = companyRepository.findById(companyId)
                            .orElseThrow(() -> ExceptionUtils.entityNotFound("회사를 찾을 수 없습니다"));
                    
                    Employee employee = new Employee();
                    employee.setEmployeeNumber(employeeNumber);
                    employee.setName(name);
                    employee.setEmail(email);
                    employee.setCompany(company);
                    employeeRepository.save(employee);
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add(String.format("%d행: %s", i + 1, e.getMessage()));
                }
            }
        } catch (IOException e) {
            log.error("엑셀 가져오기 실패", e);
            throw new RuntimeException("엑셀 가져오기 실패", e);
        }
        
        return new ImportResult(totalRows, successCount, totalRows - successCount, errors);
    }

    @Override
    @Transactional
    public ImportResult importFromCsv(MultipartFile file, Long companyId) {
        log.info("CSV 가져오기: 회사 ID {}, 파일명 {}", companyId, file.getOriginalFilename());
        
        if (companyId == null) {
            throw new IllegalArgumentException("가져오기 시 회사 ID는 필수입니다");
        }
        
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;
        
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            
            String[] line;
            try {
                while ((line = csvReader.readNext()) != null) {
                    totalRows++;
                    
                    try {
                        if (line.length < 3) {
                            errors.add(String.format("%d행: 필수 정보가 부족합니다", totalRows));
                            continue;
                        }
                        
                        String employeeNumber = line[0] != null ? line[0].trim() : "";
                        String name = line[1] != null ? line[1].trim() : "";
                        String email = line[2] != null ? line[2].trim() : "";
                        
                        if (employeeNumber.isEmpty()) {
                            errors.add(String.format("%d행: 사번이 없습니다", totalRows));
                            continue;
                        }
                        
                        // 중복 체크
                        if (employeeRepository.existsByEmployeeNumber(employeeNumber)) {
                            errors.add(String.format("%d행: 사번 %s가 이미 존재합니다", totalRows, employeeNumber));
                            continue;
                        }
                        
                        Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> ExceptionUtils.entityNotFound("회사를 찾을 수 없습니다"));
                        
                        Employee employee = new Employee();
                        employee.setEmployeeNumber(employeeNumber);
                        employee.setName(name);
                        employee.setEmail(email);
                        employee.setCompany(company);
                        employeeRepository.save(employee);
                        
                        successCount++;
                    } catch (Exception e) {
                        errors.add(String.format("%d행: %s", totalRows, e.getMessage()));
                    }
                }
            } catch (CsvValidationException e) {
                errors.add("CSV 파일 형식 오류: " + e.getMessage());
            }
        } catch (IOException e) {
            log.error("CSV 가져오기 실패", e);
            throw new RuntimeException("CSV 가져오기 실패", e);
        }
        
        return new ImportResult(totalRows, successCount, totalRows - successCount, errors);
    }

    @Override
    public Map<String, Long> getEmployeeCountsByAllStatuses() {
        List<Object[]> results = employeeRepository.countByAllEmploymentStatuses();
        Map<String, Long> counts = new HashMap<>();
        
        // 모든 상태를 0으로 초기화
        for (Employee.EmploymentStatus status : Employee.EmploymentStatus.values()) {
            counts.put(status.name(), 0L);
        }
        
        // 실제 데이터로 덮어쓰기
        for (Object[] result : results) {
            Employee.EmploymentStatus status = (Employee.EmploymentStatus) result[0];
            Long count = (Long) result[1];
            counts.put(status.name(), count);
        }
        
        return counts;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
