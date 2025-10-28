package com.erp.hr.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.DepartmentDto;
import com.erp.hr.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 부서 관리 REST Controller
 * 부서 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "부서 관리", description = "부서 정보 관리 API")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 전체 부서 목록 조회 (페이징)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 목록 조회", description = "전체 부서 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<DepartmentDto>>> getAllDepartments(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        try {
            log.info("전체 부서 목록 조회 요청: 페이지 {}, 크기 {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
            Page<DepartmentDto> departments = departmentService.getAllDepartments(pageable);
            return ResponseEntity.ok(ApiResponse.success("부서 목록 조회 완료", departments));
        } catch (Exception e) {
            log.error("전체 부서 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 회사별 부서 목록 조회 (페이징)
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사별 부서 목록 조회", description = "특정 회사의 부서 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<DepartmentDto>>> getDepartmentsByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        try {
            log.info("회사별 부서 목록 조회 요청: 회사 ID {}", companyId);
            Page<DepartmentDto> departments = departmentService.getDepartmentsByCompany(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("회사별 부서 목록 조회 완료", departments));
        } catch (Exception e) {
            log.error("회사별 부서 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사별 부서 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 부서 상세 조회
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")  // 개발/테스트용으로 임시 비활성화
    @Operation(summary = "부서 상세 조회", description = "부서 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartment(
            @Parameter(description = "부서 ID") @PathVariable Long id) {
        try {
            log.info("부서 상세 조회 요청: ID {}", id);
            DepartmentDto department = departmentService.getDepartment(id);
            return ResponseEntity.ok(ApiResponse.success("부서 정보 조회 완료", department));
        } catch (Exception e) {
            log.error("부서 상세 조회 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 부서 등록
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다")
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(
            @Valid @RequestBody DepartmentDto.DepartmentCreateDto createDto) {
        try {
            log.info("부서 등록 요청: {}", createDto.name());
            DepartmentDto department = departmentService.createDepartment(createDto);
            return ResponseEntity.ok(ApiResponse.success("부서가 성공적으로 등록되었습니다", department));
        } catch (Exception e) {
            log.error("부서 등록 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 등록에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 부서 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 수정", description = "부서 정보를 수정합니다")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(
            @Parameter(description = "부서 ID") @PathVariable Long id,
            @Valid @RequestBody DepartmentDto.DepartmentUpdateDto updateDto) {
        try {
            log.info("부서 수정 요청: ID {}", id);
            DepartmentDto department = departmentService.updateDepartment(id, updateDto);
            return ResponseEntity.ok(ApiResponse.success("부서가 성공적으로 수정되었습니다", department));
        } catch (Exception e) {
            log.error("부서 수정 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 부서 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 삭제", description = "부서를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @Parameter(description = "부서 ID") @PathVariable Long id) {
        try {
            log.info("부서 삭제 요청: ID {}", id);
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(ApiResponse.success("부서가 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("부서 삭제 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 부서 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 검색", description = "부서명으로 부서를 검색합니다")
    public ResponseEntity<ApiResponse<Page<DepartmentDto>>> searchDepartments(
            @Parameter(description = "검색어") @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        try {
            log.info("부서 검색 요청: {}", searchTerm);
            Page<DepartmentDto> departments = departmentService.searchDepartments(searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success("부서 검색 완료", departments));
        } catch (Exception e) {
            log.error("부서 검색 실패: {}, 오류: {}", searchTerm, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 회사별 부서 검색
     */
    @GetMapping("/search/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사별 부서 검색", description = "특정 회사의 부서를 검색합니다")
    public ResponseEntity<ApiResponse<Page<DepartmentDto>>> searchDepartmentsByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "검색어") @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        try {
            log.info("회사별 부서 검색 요청: 회사 ID {}, 검색어 {}", companyId, searchTerm);
            Page<DepartmentDto> departments = departmentService.searchDepartmentsByCompany(
                    companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success("회사별 부서 검색 완료", departments));
        } catch (Exception e) {
            log.error("회사별 부서 검색 실패: 회사 ID {}, 검색어 {}, 오류: {}", 
                    companyId, searchTerm, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사별 부서 검색에 실패했습니다: " + e.getMessage()));
        }
    }
}
