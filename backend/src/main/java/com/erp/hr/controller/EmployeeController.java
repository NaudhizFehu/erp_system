package com.erp.hr.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.hr.dto.EmployeeCreateDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.hr.dto.EmployeeUpdateDto;
import com.erp.hr.dto.ImportResult;
import com.erp.hr.entity.Employee;
import com.erp.hr.service.EmployeeService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse; // 별칭 대신 완전한 클래스명 사용
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.erp.common.security.UserPrincipal;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 직원 관리 REST 컨트롤러
 * 직원 관련 API 엔드포인트를 제공합니다
 */
@RestController
@RequestMapping("/api/hr/employees")
@Tag(name = "인사관리", description = "직원, 급여, 근태 관리 API")
@SecurityRequirement(name = "bearer-jwt")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    /**
     * 새로운 직원 등록
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployee(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            log.info("직원 조회 요청: ID {}, 사용자: {}", id, userPrincipal.getUsername());
            
            // USER 권한이면 본인 직원 정보만 조회 가능
            if (userPrincipal.hasRole("ROLE_USER")) {
                Long userEmployeeId = userPrincipal.getEmployeeId();
                
                // employeeId가 없으면 본인 직원 정보 조회 불가
                if (userEmployeeId == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("직원 정보가 연결되어 있지 않습니다"));
                }
                
                // 본인이 아닌 다른 직원 정보 조회 시도 시 거부
                if (!id.equals(userEmployeeId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("본인의 정보만 조회할 수 있습니다"));
                }
            }
            // SUPER_ADMIN, ADMIN, MANAGER는 권한 범위 내 모든 직원 조회 가능
            
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
     * SUPER_ADMIN은 모든 회사 직원 조회, 나머지는 자사 직원만 조회
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getAllEmployees(
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable,
            @RequestParam(required = false) Employee.EmploymentStatus employmentStatus,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            log.info("전체 직원 목록 조회 요청: 페이지 {}, 크기 {}, 상태 {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), employmentStatus);
            
            // SUPER_ADMIN이 아니면 자사 직원만 조회
            if (!userPrincipal.isSuperAdmin()) {
                Long companyId = userPrincipal.getCompanyId();
                if (companyId == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("회사 정보가 없습니다"));
                }
                log.info("자사 직원만 조회: 회사 ID {}, 상태 {}", companyId, employmentStatus);
                Page<EmployeeDto> employees = employeeService.getEmployeesByCompany(companyId, pageable, employmentStatus);
                return ResponseEntity.ok(ApiResponse.success(employees));
            }
            
            // SUPER_ADMIN은 모든 직원 조회
            Page<EmployeeDto> employees = employeeService.getAllEmployees(pageable, employmentStatus);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("전체 직원 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직원 목록 조회 (페이징)
     * SUPER_ADMIN은 모든 회사 조회 가능, 나머지는 자사만 조회 가능
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getEmployeesByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "employeeNumber") Pageable pageable,
            @RequestParam(required = false) Employee.EmploymentStatus employmentStatus,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            log.info("회사별 직원 목록 조회 요청: 회사 ID {}, 상태 {}", companyId, employmentStatus);
            
            // SUPER_ADMIN이 아니면 자사 데이터만 조회 가능
            if (!userPrincipal.isSuperAdmin() && !userPrincipal.belongsToCompany(companyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("다른 회사의 직원을 조회할 수 없습니다"));
            }
            
            Page<EmployeeDto> employees = employeeService.getEmployeesByCompany(companyId, pageable, employmentStatus);
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    /**
     * 직원 정보 삭제
     * SUPER_ADMIN은 모든 직원 삭제 가능, ADMIN은 자사 직원만 삭제 가능
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            log.info("직원 삭제 요청: ID {}", id);
            
            // SUPER_ADMIN이 아니면 자사 직원만 삭제 가능
            if (!userPrincipal.isSuperAdmin()) {
                EmployeeDto employee = employeeService.getEmployee(id);
                if (!userPrincipal.belongsToCompany(employee.company().id())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("다른 회사의 직원을 삭제할 수 없습니다"));
                }
            }
            
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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

    /**
     * 회사별 최근 직원 목록 조회 (사번 중복 방지용)
     */
    @GetMapping("/recent/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getRecentEmployeesByCompany(
            @PathVariable Long companyId) {
        try {
            log.info("회사별 최근 직원 목록 조회 요청: 회사 ID {}", companyId);
            List<EmployeeDto> employees = employeeService.getRecentEmployeesByCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            log.error("회사별 최근 직원 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 엑셀로 직원 데이터 내보내기
     */
    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> exportToExcel(
        @RequestParam(required = false) Long companyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        try {
            log.info("엑셀 내보내기 요청: 회사 ID {}, 사용자: {}", companyId, userPrincipal.getUsername());
            
            // SUPER_ADMIN: companyId null 허용 (전체), 있으면 해당 회사
            // ADMIN/MANAGER: 자동으로 자사 companyId 사용
            Long targetCompanyId = companyId;
            if (!userPrincipal.hasRole("ROLE_SUPER_ADMIN")) {
                targetCompanyId = userPrincipal.getCompanyId();
            }
            
            org.springframework.core.io.ByteArrayResource resource = employeeService.exportToExcel(targetCompanyId);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=employees_" + LocalDate.now() + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
        } catch (Exception e) {
            log.error("엑셀 내보내기 실패", e);
            throw e;
        }
    }

    /**
     * CSV로 직원 데이터 내보내기
     */
    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> exportToCsv(
        @RequestParam(required = false) Long companyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        try {
            log.info("CSV 내보내기 요청: 회사 ID {}, 사용자: {}", companyId, userPrincipal.getUsername());
            
            Long targetCompanyId = companyId;
            if (!userPrincipal.hasRole("ROLE_SUPER_ADMIN")) {
                targetCompanyId = userPrincipal.getCompanyId();
            }
            
            org.springframework.core.io.ByteArrayResource resource = employeeService.exportToCsv(targetCompanyId);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=employees_" + LocalDate.now() + ".csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
        } catch (Exception e) {
            log.error("CSV 내보내기 실패", e);
            throw e;
        }
    }

    /**
     * 엑셀에서 직원 데이터 가져오기
     */
    @PostMapping("/import/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ImportResult>> importFromExcel(
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) Long companyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        try {
            log.info("엑셀 가져오기 요청: 파일명 {}, 회사 ID {}, 사용자: {}", 
                file.getOriginalFilename(), companyId, userPrincipal.getUsername());
            
            // companyId 결정 로직
            Long targetCompanyId = companyId;
            if (!userPrincipal.hasRole("ROLE_SUPER_ADMIN")) {
                // ADMIN/MANAGER는 자사만 가능
                targetCompanyId = userPrincipal.getCompanyId();
            }
            
            // SUPER_ADMIN도 가져오기 시 companyId 필수
            if (targetCompanyId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("가져오기 시 회사를 선택해야 합니다"));
            }
            
            ImportResult result = employeeService.importFromExcel(file, targetCompanyId);
            return ResponseEntity.ok(ApiResponse.success("가져오기 완료", result));
        } catch (Exception e) {
            log.error("엑셀 가져오기 실패", e);
            throw e;
        }
    }

    /**
     * CSV에서 직원 데이터 가져오기
     */
    @PostMapping("/import/csv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ImportResult>> importFromCsv(
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) Long companyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        try {
            log.info("CSV 가져오기 요청: 파일명 {}, 회사 ID {}, 사용자: {}", 
                file.getOriginalFilename(), companyId, userPrincipal.getUsername());
            
            Long targetCompanyId = companyId;
            if (!userPrincipal.hasRole("ROLE_SUPER_ADMIN")) {
                targetCompanyId = userPrincipal.getCompanyId();
            }
            
            // SUPER_ADMIN도 가져오기 시 companyId 필수
            if (targetCompanyId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("가져오기 시 회사를 선택해야 합니다"));
            }
            
            ImportResult result = employeeService.importFromCsv(file, targetCompanyId);
            return ResponseEntity.ok(ApiResponse.success("가져오기 완료", result));
        } catch (Exception e) {
            log.error("CSV 가져오기 실패", e);
            throw e;
        }
    }

    /**
     * 모든 재직 상태별 직원 수 조회
     */
    @GetMapping("/count/by-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "상태별 직원 수 조회", description = "모든 재직 상태별 직원 수를 조회합니다")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getEmployeeCountsByStatus() {
        try {
            log.info("상태별 직원 수 조회 요청");
            Map<String, Long> counts = employeeService.getEmployeeCountsByAllStatuses();
            return ResponseEntity.ok(ApiResponse.success(counts));
        } catch (Exception e) {
            log.error("상태별 직원 수 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}