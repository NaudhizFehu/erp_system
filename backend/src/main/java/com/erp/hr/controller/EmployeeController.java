package com.erp.hr.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.hr.dto.EmployeeCreateDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import com.erp.hr.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse; // 별칭 대신 완전한 클래스명 사용
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 직원 관리 REST 컨트롤러
 * 직원 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/hr/employees")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@Tag(name = "인사관리", description = "직원, 급여, 근태 관리 API")
@SecurityRequirement(name = "bearer-jwt")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 새로운 직원 등록
     */
    @Operation(
        summary = "직원 등록",
        description = """
            새로운 직원을 시스템에 등록합니다.
            
            **주요 기능:**
            - 직원 기본 정보 등록
            - 사번 자동 생성 또는 수동 입력
            - 부서 및 직급 할당
            - 급여 정보 설정
            - 입사일 및 계약 정보 등록
            
            **권한 요구사항:**
            - ADMIN 권한 필요
            
            **유효성 검증:**
            - 사번 중복 확인
            - 이메일 형식 검증
            - 휴대폰 번호 형식 검증
            - 입사일은 현재 날짜 이전이어야 함
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "직원 등록 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "직원 등록 성공 응답",
                    value = """
                        {
                          "success": true,
                          "message": "직원이 성공적으로 등록되었습니다",
                          "data": {
                            "id": 1,
                            "employeeNumber": "EMP001",
                            "name": "홍길동",
                            "email": "hong@company.com",
                            "phoneNumber": "010-1234-5678",
                            "departmentId": 1,
                            "departmentName": "개발팀",
                            "positionId": 1,
                            "positionName": "대리",
                            "hireDate": "2023-12-01",
                            "salary": 4000000,
                            "status": "ACTIVE",
                            "createdAt": "2023-12-01T10:30:00Z",
                            "updatedAt": "2023-12-01T10:30:00Z"
                          },
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "유효성 검증 실패 응답",
                    value = """
                        {
                          "success": false,
                          "message": "입력 데이터가 올바르지 않습니다",
                          "error": "이미 존재하는 사번입니다",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "권한 없음 응답",
                    value = """
                        {
                          "success": false,
                          "message": "접근 권한이 없습니다",
                          "error": "ADMIN 권한이 필요합니다",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(
        @Parameter(
            description = "직원 등록 정보",
            required = true,
            schema = @Schema(implementation = EmployeeCreateDto.class),
            example = """
                {
                  "employeeNumber": "EMP001",
                  "name": "홍길동",
                  "email": "hong@company.com",
                  "phoneNumber": "010-1234-5678",
                  "address": "서울시 강남구",
                  "birthDate": "1990-01-01",
                  "gender": "MALE",
                  "departmentId": 1,
                  "positionId": 1,
                  "hireDate": "2023-12-01",
                  "contractType": "FULL_TIME",
                  "salary": 4000000,
                  "bankAccount": "1234-567-890123",
                  "bankName": "국민은행",
                  "emergencyContact": "010-9876-5432",
                  "emergencyContactName": "홍부모"
                }
                """
        )
        @Valid @RequestBody EmployeeCreateDto createDto) {
        try {
            log.info("직원 등록 요청: {}", createDto.employeeNumber());
            EmployeeDto employee = employeeService.createEmployee(createDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("직원이 성공적으로 등록되었습니다", employee));
        } catch (Exception e) {
            log.error("직원 등록 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 정보 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDto updateDto) {
        try {
            log.info("직원 정보 수정 요청: ID {}", id);
            EmployeeDto employee = employeeService.updateEmployee(id, updateDto);
            return ResponseEntity.ok(ApiResponse.success("직원 정보가 성공적으로 수정되었습니다", employee));
        } catch (Exception e) {
            log.error("직원 정보 수정 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 정보 조회 (ID)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployee(@PathVariable Long id) {
        try {
            log.info("직원 조회 요청: ID {}", id);
            EmployeeDto employee = employeeService.getEmployee(id);
            return ResponseEntity.ok(ApiResponse.success(employee));
        } catch (Exception e) {
            log.error("직원 조회 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사번으로 직원 조회
     */
    @GetMapping("/number/{employeeNumber}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeByNumber(
            @PathVariable String employeeNumber) {
        try {
            log.info("사번으로 직원 조회 요청: {}", employeeNumber);
            EmployeeDto employee = employeeService.getEmployeeByNumber(employeeNumber);
            return ResponseEntity.ok(ApiResponse.success(employee));
        } catch (Exception e) {
            log.error("사번으로 직원 조회 실패: {}, 오류: {}", employeeNumber, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 이메일로 직원 조회
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeByEmail(@PathVariable String email) {
        try {
            log.info("이메일로 직원 조회 요청: {}", email);
            EmployeeDto employee = employeeService.getEmployeeByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(employee));
        } catch (Exception e) {
            log.error("이메일로 직원 조회 실패: {}, 오류: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 전체 직원 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getAllEmployees(
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable) {
        try {
            log.info("전체 직원 목록 조회 요청: 페이지 {}, 크기 {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
            Page<EmployeeDto> employees = employeeService.getAllEmployees(pageable);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("전체 직원 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직원 목록 조회 (페이징)
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getEmployeesByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable) {
        try {
            log.info("회사별 직원 목록 조회 요청: 회사 ID {}", companyId);
            Page<EmployeeDto> employees = employeeService.getEmployeesByCompany(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("회사별 직원 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 부서별 직원 목록 조회 (페이징)
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable) {
        try {
            log.info("부서별 직원 목록 조회 요청: 부서 ID {}", departmentId);
            Page<EmployeeDto> employees = employeeService.getEmployeesByDepartment(departmentId, pageable);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("부서별 직원 목록 조회 실패: 부서 ID {}, 오류: {}", departmentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 재직 중인 직원 목록 조회
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getActiveEmployees() {
        try {
            log.info("재직 중인 직원 목록 조회 요청");
            List<EmployeeDto> employees = employeeService.getActiveEmployees();
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("재직 중인 직원 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 재직 중인 직원 목록 조회
     */
    @GetMapping("/active/company/{companyId}")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getActiveEmployeesByCompany(
            @PathVariable Long companyId) {
        try {
            log.info("회사별 재직 중인 직원 목록 조회 요청: 회사 ID {}", companyId);
            List<EmployeeDto> employees = employeeService.getActiveEmployeesByCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("회사별 재직 중인 직원 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> searchEmployees(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable) {
        try {
            log.info("직원 검색 요청: {}", searchTerm);
            Page<EmployeeDto> employees = employeeService.searchEmployees(searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("직원 검색 실패: {}, 오류: {}", searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직원 검색
     */
    @GetMapping("/search/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> searchEmployeesByCompany(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable) {
        try {
            log.info("회사별 직원 검색 요청: 회사 ID {}, 검색어 {}", companyId, searchTerm);
            Page<EmployeeDto> employees = employeeService.searchEmployeesByCompany(
                    companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("회사별 직원 검색 실패: 회사 ID {}, 검색어 {}, 오류: {}", 
                    companyId, searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 입사일 범위로 직원 조회
     */
    @GetMapping("/hire-date")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getEmployeesByHireDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("입사일 범위 직원 조회 요청: {} ~ {}", startDate, endDate);
            List<EmployeeDto> employees = employeeService.getEmployeesByHireDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("입사일 범위 직원 조회 실패: {} ~ {}, 오류: {}", startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 생일인 직원 조회
     */
    @GetMapping("/birthday")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getEmployeesByBirthday(
            @RequestParam int month,
            @RequestParam int day) {
        try {
            log.info("생일 직원 조회 요청: {}월 {}일", month, day);
            List<EmployeeDto> employees = employeeService.getEmployeesByBirthday(month, day);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("생일 직원 조회 실패: {}월 {}일, 오류: {}", month, day, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 이번 달 생일인 직원 조회
     */
    @GetMapping("/birthday/this-month")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getBirthdayEmployeesThisMonth() {
        try {
            log.info("이번 달 생일 직원 조회 요청");
            List<EmployeeDto> employees = employeeService.getBirthdayEmployeesThisMonth();
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("이번 달 생일 직원 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 퇴직 처리
     */
    @PatchMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> terminateEmployee(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate,
            @RequestParam String reason) {
        try {
            log.info("직원 퇴직 처리 요청: ID {}, 퇴사일 {}", id, terminationDate);
            employeeService.terminateEmployee(id, terminationDate, reason);
            return ResponseEntity.ok(ApiResponse.success("직원 퇴직 처리가 완료되었습니다"));
        } catch (Exception e) {
            log.error("직원 퇴직 처리 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 복직 처리
     */
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reactivateEmployee(@PathVariable Long id) {
        try {
            log.info("직원 복직 처리 요청: ID {}", id);
            employeeService.reactivateEmployee(id);
            return ResponseEntity.ok(ApiResponse.success("직원 복직 처리가 완료되었습니다"));
        } catch (Exception e) {
            log.error("직원 복직 처리 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직원 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        try {
            log.info("직원 삭제 요청: ID {}", id);
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok(ApiResponse.success("직원이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("직원 삭제 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사번 중복 확인
     */
    @GetMapping("/check/employee-number")
    public ResponseEntity<ApiResponse<Boolean>> checkEmployeeNumber(
            @RequestParam String employeeNumber,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("사번 중복 확인 요청: {}", employeeNumber);
            boolean exists = excludeId != null 
                    ? employeeService.isEmployeeNumberExists(employeeNumber, excludeId)
                    : employeeService.isEmployeeNumberExists(employeeNumber);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("사번 중복 확인 실패: {}, 오류: {}", employeeNumber, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("이메일 중복 확인 요청: {}", email);
            boolean exists = excludeId != null
                    ? employeeService.isEmailExists(email, excludeId)
                    : employeeService.isEmailExists(email);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("이메일 중복 확인 실패: {}, 오류: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급별 직원 수 통계
     */
    @GetMapping("/statistics/position")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEmployeeCountByPosition() {
        try {
            log.info("직급별 직원 수 통계 조회 요청");
            List<Object[]> statistics = employeeService.getEmployeeCountByPosition();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("직급별 직원 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 부서별 직원 수 통계
     */
    @GetMapping("/statistics/department")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEmployeeCountByDepartment() {
        try {
            log.info("부서별 직원 수 통계 조회 요청");
            List<Object[]> statistics = employeeService.getEmployeeCountByDepartment();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("부서별 직원 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 입사년도별 직원 수 통계
     */
    @GetMapping("/statistics/hire-year")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEmployeeCountByHireYear() {
        try {
            log.info("입사년도별 직원 수 통계 조회 요청");
            List<Object[]> statistics = employeeService.getEmployeeCountByHireYear();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("입사년도별 직원 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 연령대별 직원 수 통계
     */
    @GetMapping("/statistics/age-group")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEmployeeCountByAgeGroup() {
        try {
            log.info("연령대별 직원 수 통계 조회 요청");
            List<Object[]> statistics = employeeService.getEmployeeCountByAgeGroup();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("연령대별 직원 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 성별 직원 수 통계
     */
    @GetMapping("/statistics/gender")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEmployeeCountByGender() {
        try {
            log.info("성별 직원 수 통계 조회 요청");
            List<Object[]> statistics = employeeService.getEmployeeCountByGender();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("성별 직원 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}