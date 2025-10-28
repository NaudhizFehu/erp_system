package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.CompanyDto;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
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

import java.util.List;
import java.util.Optional;

/**
 * 회사 관리 REST Controller
 * 회사 정보 조회 및 관리를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "회사 관리", description = "회사 정보 관리 API")
public class CompanyController {

    private final CompanyRepository companyRepository;

    /**
     * 전체 회사 목록 조회 (페이징)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사 목록 조회", description = "등록된 모든 회사 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<CompanyDto>>> getAllCompanies(
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사 목록 조회 요청: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
            Page<Company> companies = companyRepository.findAllActive(pageable);
            
            // Company 엔티티를 CompanyDto로 변환
            Page<CompanyDto> companyDtos = companies.map(CompanyDto::from);
            
            return ResponseEntity.ok(ApiResponse.success("회사 목록 조회 완료", companyDtos));
        } catch (Exception e) {
            log.error("회사 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 회사 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사 상세 조회", description = "특정 회사의 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<CompanyDto>> getCompany(
            @Parameter(description = "회사 ID") @PathVariable Long id) {
        try {
            log.info("회사 상세 조회 요청: ID {}", id);
            Optional<Company> company = companyRepository.findById(id);
            
            if (company.isPresent()) {
                Company companyData = company.get();
                // isDeleted 체크를 안전하게 처리
                if (companyData.getIsDeleted() == null || !companyData.getIsDeleted()) {
                    CompanyDto companyDto = CompanyDto.from(companyData);
                    return ResponseEntity.ok(ApiResponse.success("회사 정보 조회 완료", companyDto));
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("회사 상세 조회 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사 정보 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 회사 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "회사 검색", description = "회사명으로 회사를 검색합니다")
    public ResponseEntity<ApiResponse<List<Company>>> searchCompanies(
            @Parameter(description = "검색어") @RequestParam String q) {
        try {
            log.info("회사 검색 요청: query={}", q);
            List<Company> companies = companyRepository.findByNameContaining(q);
            return ResponseEntity.ok(ApiResponse.success("회사 검색 완료", companies));
        } catch (Exception e) {
            log.error("회사 검색 실패: query={}, 오류: {}", q, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회사 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 활성 회사 목록 조회
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "활성 회사 목록", description = "활성 상태인 회사 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<Company>>> getActiveCompanies() {
        try {
            log.info("활성 회사 목록 조회 요청");
            List<Company> companies = companyRepository.findActiveCompanies();
            return ResponseEntity.ok(ApiResponse.success("활성 회사 목록 조회 완료", companies));
        } catch (Exception e) {
            log.error("활성 회사 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("활성 회사 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }
}
