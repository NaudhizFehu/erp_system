package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.GlobalSearchDto;
import com.erp.common.service.GlobalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 전역 검색 REST Controller
 * 모든 모듈에서 통합 검색을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "전역 검색", description = "통합 검색 API")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "전역 검색", description = "모든 모듈에서 검색어로 통합 검색을 수행합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> globalSearch(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("전역 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchAll(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("검색 완료", results));
        } catch (Exception e) {
            log.error("전역 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "고객 검색", description = "고객을 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchCustomers(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("고객 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchCustomers(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("고객 검색 완료", results));
        } catch (Exception e) {
            log.error("고객 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "직원 검색", description = "직원을 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchEmployees(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("직원 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchEmployees(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("직원 검색 완료", results));
        } catch (Exception e) {
            log.error("직원 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("직원 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "상품 검색", description = "상품을 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchProducts(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("상품 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchProducts(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("상품 검색 완료", results));
        } catch (Exception e) {
            log.error("상품 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("상품 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "주문 검색", description = "주문을 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchOrders(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("주문 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchOrders(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("주문 검색 완료", results));
        } catch (Exception e) {
            log.error("주문 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/departments")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "부서 검색", description = "부서를 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchDepartments(
            @Parameter(description = "검색어") @RequestParam String q,
            @Parameter(description = "회사 ID") @RequestParam(defaultValue = "1") Long companyId) {
        try {
            log.info("부서 검색 API 호출: q={}, companyId={}", q, companyId);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchDepartments(q, companyId);
            return ResponseEntity.ok(ApiResponse.success("부서 검색 완료", results));
        } catch (Exception e) {
            log.error("부서 검색 실패: q={}, companyId={}, {}", q, companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("부서 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/companies")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사 검색", description = "회사를 검색합니다")
    public ResponseEntity<ApiResponse<List<GlobalSearchDto.SearchResult>>> searchCompanies(
            @Parameter(description = "검색어") @RequestParam String q) {
        try {
            log.info("회사 검색 API 호출: q={}", q);
            List<GlobalSearchDto.SearchResult> results = globalSearchService.searchCompanies(q);
            return ResponseEntity.ok(ApiResponse.success("회사 검색 완료", results));
        } catch (Exception e) {
            log.error("회사 검색 실패: q={}, {}", q, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사 검색에 실패했습니다: " + e.getMessage()));
        }
    }
}
