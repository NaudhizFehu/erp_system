package com.erp.accounting.controller;

import com.erp.accounting.dto.*;
import com.erp.accounting.service.AccountService;
import com.erp.common.dto.ApiResponse;
import com.erp.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting/accounts")
@Tag(name = "회계관리 - 계정과목", description = "계정과목 CRUD 및 트리 API")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    // GET /api/accounting/accounts - 목록 조회 (페이지네이션)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<AccountDto>>> getAccounts(
            @PageableDefault(size = 20, sort = "accountCode") Pageable pageable
    ) {
        try {
            Page<AccountDto> page = accountService.getAccounts(pageable);
            return ResponseEntity.ok(ApiResponse.success(page));
        } catch (Exception e) {
            log.error("계정과목 목록 조회 실패", e);
            throw e;
        }
    }

    // GET /api/accounting/accounts/{id} - 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AccountDto>> getAccount(@PathVariable Long id) {
        try {
            AccountDto dto = accountService.getAccount(id);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            log.error("계정과목 상세 조회 실패: {}", id, e);
            throw e;
        }
    }

    // POST /api/accounting/accounts - 등록
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(
            @Valid @RequestBody AccountCreateDto dto
    ) {
        try {
            AccountDto created = accountService.createAccount(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("계정과목이 성공적으로 등록되었습니다", created));
        } catch (Exception e) {
            log.error("계정과목 등록 실패", e);
            throw e;
        }
    }

    // PUT /api/accounting/accounts/{id} - 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateDto dto
    ) {
        try {
            AccountDto updated = accountService.updateAccount(id, dto);
            return ResponseEntity.ok(ApiResponse.success("계정과목이 성공적으로 수정되었습니다", updated));
        } catch (Exception e) {
            log.error("계정과목 수정 실패: {}", id, e);
            throw e;
        }
    }

    // DELETE /api/accounting/accounts/{id} - 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok(ApiResponse.success("계정과목이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("계정과목 삭제 실패: {}", id, e);
            throw e;
        }
    }

    // GET /api/accounting/accounts/tree - 트리 구조 조회 (회사 기준)
    @GetMapping("/tree")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<AccountTreeNodeDto>>> getAccountTree(
            @RequestParam(required = false) Long companyId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        try {
            Long targetCompanyId = companyId;
            if (targetCompanyId == null && userPrincipal != null && !userPrincipal.isSuperAdmin()) {
                targetCompanyId = userPrincipal.getCompanyId();
            }
            if (targetCompanyId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("회사 ID가 필요합니다"));
            }
            List<AccountTreeNodeDto> tree = accountService.getAccountTree(targetCompanyId);
            return ResponseEntity.ok(ApiResponse.success(tree));
        } catch (Exception e) {
            log.error("계정과목 트리 조회 실패", e);
            throw e;
        }
    }

    // GET /api/accounting/accounts/check/code - 계정코드 중복 확인
    @GetMapping("/check/code")
    public ResponseEntity<ApiResponse<Boolean>> checkAccountCode(
            @RequestParam String accountCode,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long excludeId
    ) {
        try {
            boolean exists = accountService.isAccountCodeExists(accountCode, companyId, excludeId);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("계정코드 중복 확인 실패: {}", accountCode, e);
            throw e;
        }
    }
}




