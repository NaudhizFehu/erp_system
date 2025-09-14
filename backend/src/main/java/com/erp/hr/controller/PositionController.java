package com.erp.hr.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.hr.dto.PositionCreateDto;
import com.erp.hr.dto.PositionDto;
import com.erp.hr.dto.PositionUpdateDto;
import com.erp.hr.entity.Position;
import com.erp.hr.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 직급 관리 REST 컨트롤러
 * 직급 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/hr/positions")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class PositionController {

    @Autowired
    private PositionService positionService;

    /**
     * 새로운 직급 등록
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PositionDto>> createPosition(
            @Valid @RequestBody PositionCreateDto createDto) {
        try {
            log.info("직급 등록 요청: {}", createDto.positionCode());
            PositionDto position = positionService.createPosition(createDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("직급이 성공적으로 등록되었습니다", position));
        } catch (Exception e) {
            log.error("직급 등록 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 정보 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PositionDto>> updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody PositionUpdateDto updateDto) {
        try {
            log.info("직급 정보 수정 요청: ID {}", id);
            PositionDto position = positionService.updatePosition(id, updateDto);
            return ResponseEntity.ok(ApiResponse.success("직급 정보가 성공적으로 수정되었습니다", position));
        } catch (Exception e) {
            log.error("직급 정보 수정 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 정보 조회 (ID)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PositionDto>> getPosition(@PathVariable Long id) {
        try {
            log.info("직급 조회 요청: ID {}", id);
            PositionDto position = positionService.getPosition(id);
            return ResponseEntity.ok(ApiResponse.success(position));
        } catch (Exception e) {
            log.error("직급 조회 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 코드로 직급 조회
     */
    @GetMapping("/code/{positionCode}")
    public ResponseEntity<ApiResponse<PositionDto>> getPositionByCode(
            @PathVariable String positionCode) {
        try {
            log.info("직급 코드로 조회 요청: {}", positionCode);
            PositionDto position = positionService.getPositionByCode(positionCode);
            return ResponseEntity.ok(ApiResponse.success(position));
        } catch (Exception e) {
            log.error("직급 코드 조회 실패: {}, 오류: {}", positionCode, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 전체 직급 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PositionDto>>> getAllPositions(
            @PageableDefault(size = 20, sort = "positionLevel") Pageable pageable) {
        try {
            log.info("전체 직급 목록 조회 요청: 페이지 {}, 크기 {}", 
                    pageable.getPageNumber(), pageable.getPageSize());
            Page<PositionDto> positions = positionService.getAllPositions(pageable);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("전체 직급 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직급 목록 조회
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPositionsByCompany(
            @PathVariable Long companyId) {
        try {
            log.info("회사별 직급 목록 조회 요청: 회사 ID {}", companyId);
            List<PositionDto> positions = positionService.getActivePositionsByCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("회사별 직급 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 활성 직급 목록 조회
     */
    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getActivePositionsByCompany(
            @PathVariable Long companyId) {
        try {
            log.info("회사별 활성 직급 목록 조회 요청: 회사 ID {}", companyId);
            List<PositionDto> positions = positionService.getActivePositionsByCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("회사별 활성 직급 목록 조회 실패: 회사 ID {}, 오류: {}", companyId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 레벨별 조회
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPositionsByLevel(
            @PathVariable Integer level) {
        try {
            log.info("직급 레벨별 조회 요청: 레벨 {}", level);
            List<PositionDto> positions = positionService.getPositionsByLevel(level);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("직급 레벨별 조회 실패: 레벨 {}, 오류: {}", level, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 분류별 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPositionsByCategory(
            @PathVariable Position.PositionCategory category) {
        try {
            log.info("직급 분류별 조회 요청: 분류 {}", category);
            List<PositionDto> positions = positionService.getPositionsByCategory(category);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("직급 분류별 조회 실패: 분류 {}, 오류: {}", category, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 유형별 조회
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPositionsByType(
            @PathVariable Position.PositionType type) {
        try {
            log.info("직급 유형별 조회 요청: 유형 {}", type);
            List<PositionDto> positions = positionService.getPositionsByType(type);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("직급 유형별 조회 실패: 유형 {}, 오류: {}", type, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 활성 직급 목록 조회
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getActivePositions() {
        try {
            log.info("활성 직급 목록 조회 요청");
            List<PositionDto> positions = positionService.getActivePositions();
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("활성 직급 목록 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PositionDto>>> searchPositions(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "positionLevel") Pageable pageable) {
        try {
            log.info("직급 검색 요청: {}", searchTerm);
            Page<PositionDto> positions = positionService.searchPositions(searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("직급 검색 실패: {}, 오류: {}", searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직급 검색
     */
    @GetMapping("/search/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<PositionDto>>> searchPositionsByCompany(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "positionLevel") Pageable pageable) {
        try {
            log.info("회사별 직급 검색 요청: 회사 ID {}, 검색어 {}", companyId, searchTerm);
            Page<PositionDto> positions = positionService.searchPositionsByCompany(
                    companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("회사별 직급 검색 실패: 회사 ID {}, 검색어 {}, 오류: {}", 
                    companyId, searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 승진 가능한 직급 조회
     */
    @GetMapping("/promotable")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPromotablePositions(
            @RequestParam Long companyId,
            @RequestParam Integer currentLevel) {
        try {
            log.info("승진 가능 직급 조회 요청: 회사 ID {}, 현재 레벨 {}", companyId, currentLevel);
            List<PositionDto> positions = positionService.getPromotablePositions(companyId, currentLevel);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("승진 가능 직급 조회 실패: 회사 ID {}, 현재 레벨 {}, 오류: {}", 
                    companyId, currentLevel, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 급여 범위별 직급 조회
     */
    @GetMapping("/salary-range")
    public ResponseEntity<ApiResponse<List<PositionDto>>> getPositionsBySalaryRange(
            @RequestParam BigDecimal salary) {
        try {
            log.info("급여 범위별 직급 조회 요청: 급여 {}", salary);
            List<PositionDto> positions = positionService.getPositionsBySalaryRange(salary);
            return ResponseEntity.ok(ApiResponse.success(positions));
        } catch (Exception e) {
            log.error("급여 범위별 직급 조회 실패: 급여 {}, 오류: {}", salary, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 활성화/비활성화 토글
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PositionDto>> togglePositionStatus(@PathVariable Long id) {
        try {
            log.info("직급 상태 토글 요청: ID {}", id);
            PositionDto position = positionService.togglePositionStatus(id);
            return ResponseEntity.ok(ApiResponse.success("직급 상태가 변경되었습니다", position));
        } catch (Exception e) {
            log.error("직급 상태 토글 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePosition(@PathVariable Long id) {
        try {
            log.info("직급 삭제 요청: ID {}", id);
            positionService.deletePosition(id);
            return ResponseEntity.ok(ApiResponse.success("직급이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("직급 삭제 실패: ID {}, 오류: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 코드 중복 확인
     */
    @GetMapping("/check/position-code")
    public ResponseEntity<ApiResponse<Boolean>> checkPositionCode(
            @RequestParam String positionCode,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("직급 코드 중복 확인 요청: {}", positionCode);
            boolean exists = excludeId != null 
                    ? positionService.isPositionCodeExists(positionCode, excludeId)
                    : positionService.isPositionCodeExists(positionCode);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("직급 코드 중복 확인 실패: {}, 오류: {}", positionCode, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직급 코드 중복 확인
     */
    @GetMapping("/check/position-code/company/{companyId}")
    public ResponseEntity<ApiResponse<Boolean>> checkPositionCodeInCompany(
            @PathVariable Long companyId,
            @RequestParam String positionCode,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("회사별 직급 코드 중복 확인 요청: 회사 ID {}, 코드 {}", companyId, positionCode);
            boolean exists = excludeId != null 
                    ? positionService.isPositionCodeExistsInCompany(companyId, positionCode, excludeId)
                    : positionService.isPositionCodeExistsInCompany(companyId, positionCode);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("회사별 직급 코드 중복 확인 실패: 회사 ID {}, 코드 {}, 오류: {}", 
                    companyId, positionCode, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 분류별 통계
     */
    @GetMapping("/statistics/category")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPositionCountByCategory() {
        try {
            log.info("직급 분류별 통계 조회 요청");
            List<Object[]> statistics = positionService.getPositionCountByCategory();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("직급 분류별 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 직급 유형별 통계
     */
    @GetMapping("/statistics/type")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPositionCountByType() {
        try {
            log.info("직급 유형별 통계 조회 요청");
            List<Object[]> statistics = positionService.getPositionCountByType();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("직급 유형별 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회사별 직급 수 통계
     */
    @GetMapping("/statistics/company")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPositionCountByCompany() {
        try {
            log.info("회사별 직급 수 통계 조회 요청");
            List<Object[]> statistics = positionService.getPositionCountByCompany();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("회사별 직급 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 레벨별 직급 수 통계
     */
    @GetMapping("/statistics/level")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPositionCountByLevel() {
        try {
            log.info("레벨별 직급 수 통계 조회 요청");
            List<Object[]> statistics = positionService.getPositionCountByLevel();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("레벨별 직급 수 통계 조회 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}
