package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.PageResponse;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 직원 관리 컨트롤러
 * ADMIN 이상 권한 사용자가 시스템 직원을 관리합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class EmployeeManagementController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 전체 직원 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmployeeDto>>> getAllEmployees(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<Employee> employees = employeeRepository.findAllWithDetails(pageable);

            List<EmployeeDto> employeeDTOs = employees.getContent().stream()
                    .map(EmployeeDto::from)
                    .collect(Collectors.toList());

            PageResponse<EmployeeDto> pageResponse = PageResponse.<EmployeeDto>builder()
                    .content(employeeDTOs)
                    .page(employees.getNumber())
                    .size(employees.getSize())
                    .totalElements(employees.getTotalElements())
                    .totalPages(employees.getTotalPages())
                    .first(employees.isFirst())
                    .last(employees.isLast())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(pageResponse));
        } catch (Exception e) {
            log.error("직원 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원 목록을 조회할 수 없습니다", null));
        }
    }

    /**
     * 직원 검색 (페이징)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeDto>>> searchEmployees(
            @RequestParam String query,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<Employee> employees = employeeRepository.searchEmployees(query, pageable);

            List<EmployeeDto> employeeDTOs = employees.getContent().stream()
                    .map(EmployeeDto::from)
                    .collect(Collectors.toList());

            PageResponse<EmployeeDto> pageResponse = PageResponse.<EmployeeDto>builder()
                    .content(employeeDTOs)
                    .page(employees.getNumber())
                    .size(employees.getSize())
                    .totalElements(employees.getTotalElements())
                    .totalPages(employees.getTotalPages())
                    .first(employees.isFirst())
                    .last(employees.isLast())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(pageResponse));
        } catch (Exception e) {
            log.error("직원 검색 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원을 검색할 수 없습니다", null));
        }
    }

    /**
     * 특정 직원 조회
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Long employeeId) {
        try {
            Employee employee = employeeRepository.findByIdWithDetails(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            return ResponseEntity.ok(ApiResponse.success(EmployeeDto.from(employee)));
        } catch (IllegalArgumentException e) {
            log.warn("직원 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원을 조회할 수 없습니다", null));
        }
    }

    /**
     * 역할별 직원 조회
     */
    @GetMapping("/by-role/{role}")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getEmployeesByRole(@PathVariable Employee.UserRole role) {
        try {
            // Employee entity에 role 필드가 있다고 가정하고 필터링
            List<Employee> allEmployees = employeeRepository.findActiveEmployees();
            List<Employee> filteredEmployees = allEmployees.stream()
                    .filter(e -> e.getRole() != null && e.getRole().equals(role))
                    .collect(Collectors.toList());

            List<EmployeeDto> employeeDTOs = filteredEmployees.stream()
                    .map(EmployeeDto::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(employeeDTOs));
        } catch (Exception e) {
            log.error("역할별 직원 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("역할별 직원을 조회할 수 없습니다", null));
        }
    }

    /**
     * 직원 계정 활성화/비활성화
     */
    @PatchMapping("/{employeeId}/active")
    public ResponseEntity<ApiResponse<EmployeeDto>> toggleEmployeeActiveStatus(
            @PathVariable Long employeeId,
            @RequestParam Boolean isActive) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            employee.setIsActive(isActive);
            Employee updatedEmployee = employeeRepository.save(employee);

            log.info("직원 계정 상태 변경: employeeId={}, isActive={}", employeeId, isActive);
            return ResponseEntity.ok(ApiResponse.success(EmployeeDto.from(updatedEmployee)));
        } catch (IllegalArgumentException e) {
            log.warn("직원 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원 상태를 변경할 수 없습니다", null));
        }
    }

    /**
     * 직원 계정 잠금/잠금해제
     */
    @PatchMapping("/{employeeId}/lock")
    public ResponseEntity<ApiResponse<EmployeeDto>> toggleEmployeeLockStatus(
            @PathVariable Long employeeId,
            @RequestParam Boolean isLocked) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            employee.setIsLocked(isLocked);
            Employee updatedEmployee = employeeRepository.save(employee);

            log.info("직원 계정 잠금 상태 변경: employeeId={}, isLocked={}", employeeId, isLocked);
            return ResponseEntity.ok(ApiResponse.success(EmployeeDto.from(updatedEmployee)));
        } catch (IllegalArgumentException e) {
            log.warn("직원 잠금 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 잠금 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원 잠금 상태를 변경할 수 없습니다", null));
        }
    }

    /**
     * 직원 역할 변경
     */
    @PatchMapping("/{employeeId}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployeeRole(
            @PathVariable Long employeeId,
            @RequestParam Employee.UserRole role) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            employee.setRole(role);
            Employee updatedEmployee = employeeRepository.save(employee);

            log.info("직원 역할 변경: employeeId={}, newRole={}", employeeId, role);
            return ResponseEntity.ok(ApiResponse.success(EmployeeDto.from(updatedEmployee)));
        } catch (IllegalArgumentException e) {
            log.warn("직원 역할 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 역할 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원 역할을 변경할 수 없습니다", null));
        }
    }

    /**
     * 직원 비밀번호 재설정
     */
    @PatchMapping("/{employeeId}/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetEmployeePassword(
            @PathVariable Long employeeId,
            @RequestParam String newPassword) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            employee.setPassword(passwordEncoder.encode(newPassword));
            employee.setPasswordChangedAt(LocalDateTime.now());
            employee.setIsPasswordExpired(true); // 다음 로그인 시 비밀번호 변경 강제
            employeeRepository.save(employee);

            log.info("직원 비밀번호 재설정: employeeId={}", employeeId);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다"));
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 재설정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("비밀번호 재설정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비밀번호를 재설정할 수 없습니다", null));
        }
    }

    /**
     * 직원 정보 수정
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeUpdateDto updateDto) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            // 수정 가능한 필드만 업데이트
            if (updateDto.name() != null) {
                employee.setName(updateDto.name());
            }
            if (updateDto.email() != null) {
                // 이메일 중복 확인
                if (employeeRepository.existsByEmailAndIdNot(updateDto.email(), employeeId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error("이미 사용 중인 이메일입니다", null));
                }
                employee.setEmail(updateDto.email());
            }
            if (updateDto.mobile() != null) {
                employee.setMobile(updateDto.mobile());
            }

            Employee updatedEmployee = employeeRepository.save(employee);
            log.info("직원 정보 수정: employeeId={}", employeeId);

            return ResponseEntity.ok(ApiResponse.success(EmployeeDto.from(updatedEmployee)));
        } catch (IllegalArgumentException e) {
            log.warn("직원 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 정보 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원 정보를 수정할 수 없습니다", null));
        }
    }

    /**
     * 직원 삭제 (논리 삭제)
     */
    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long employeeId) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("직원을 찾을 수 없습니다"));

            employee.setIsDeleted(true);
            employee.setIsActive(false);
            employeeRepository.save(employee);

            log.info("직원 삭제: employeeId={}", employeeId);
            return ResponseEntity.ok(ApiResponse.success("직원이 삭제되었습니다"));
        } catch (IllegalArgumentException e) {
            log.warn("직원 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("직원 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("직원을 삭제할 수 없습니다", null));
        }
    }
}
