package com.erp.inventory.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.inventory.dto.InventoryDto;
import com.erp.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 관리 컨트롤러
 * 재고 입출고, 실사, 조정 등의 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 재고 생성
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> createInventory(
            @Valid @RequestBody InventoryDto.InventoryCreateDto createDto) {
        try {
            log.info("재고 생성 요청 - 회사: {}, 상품: {}, 창고: {}", 
                    createDto.companyId(), createDto.productId(), createDto.warehouseId());
            
            InventoryDto.InventoryResponseDto result = inventoryService.createInventory(createDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 생성 실패 - 상품: {}, 창고: {}", createDto.productId(), createDto.warehouseId(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDto.InventoryUpdateDto updateDto) {
        try {
            log.info("재고 수정 요청 - ID: {}", id);
            
            InventoryDto.InventoryResponseDto result = inventoryService.updateInventory(id, updateDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고가 성공적으로 수정되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 수정 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 수정에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        try {
            log.info("재고 삭제 요청 - ID: {}", id);
            
            inventoryService.deleteInventory(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고가 성공적으로 삭제되었습니다"
            ));
        } catch (Exception e) {
            log.error("재고 삭제 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 삭제에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 조회 (ID)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> getInventoryById(@PathVariable Long id) {
        try {
            log.info("재고 조회 요청 - ID: {}", id);
            
            InventoryDto.InventoryResponseDto result = inventoryService.getInventoryById(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 정보를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 조회 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 재고 목록 조회
     */
    @GetMapping("/companies/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<InventoryDto.InventorySummaryDto>>> getInventoriesByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 재고 목록 조회 요청 - 회사: {}", companyId);
            
            Page<InventoryDto.InventorySummaryDto> result = inventoryService.getInventoriesByCompany(companyId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 재고 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("회사별 재고 목록 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 재고 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품별 재고 목록 조회
     */
    @GetMapping("/products/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventoryResponseDto>>> getInventoriesByProduct(@PathVariable Long productId) {
        try {
            log.info("상품별 재고 목록 조회 요청 - 상품: {}", productId);
            
            List<InventoryDto.InventoryResponseDto> result = inventoryService.getInventoriesByProduct(productId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품별 재고 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품별 재고 목록 조회 실패 - 상품: {}", productId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품별 재고 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 창고별 재고 목록 조회
     */
    @GetMapping("/warehouses/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<InventoryDto.InventorySummaryDto>>> getInventoriesByWarehouse(
            @PathVariable Long warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("창고별 재고 목록 조회 요청 - 창고: {}", warehouseId);
            
            Page<InventoryDto.InventorySummaryDto> result = inventoryService.getInventoriesByWarehouse(warehouseId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "창고별 재고 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("창고별 재고 목록 조회 실패 - 창고: {}", warehouseId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("창고별 재고 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 검색
     */
    @GetMapping("/companies/{companyId}/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<InventoryDto.InventorySummaryDto>>> searchInventories(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("재고 검색 요청 - 회사: {}, 검색어: {}", companyId, searchTerm);
            
            Page<InventoryDto.InventorySummaryDto> result = inventoryService.searchInventories(companyId, searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 검색 실패 - 회사: {}, 검색어: {}", companyId, searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 입고 처리
     */
    @PostMapping("/{id}/receive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> receiveStock(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestParam BigDecimal unitCost,
            @RequestParam(required = false) String reason) {
        try {
            log.info("재고 입고 처리 요청 - 재고 ID: {}, 수량: {}", id, quantity);
            
            InventoryDto.InventoryResponseDto result = inventoryService.receiveStock(id, quantity, unitCost, reason);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 입고가 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 입고 처리 실패 - 재고 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 입고 처리에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 출고 처리
     */
    @PostMapping("/{id}/issue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> issueStock(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestParam(required = false) String reason) {
        try {
            log.info("재고 출고 처리 요청 - 재고 ID: {}, 수량: {}", id, quantity);
            
            InventoryDto.InventoryResponseDto result = inventoryService.issueStock(id, quantity, reason);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 출고가 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 출고 처리 실패 - 재고 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 출고 처리에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 예약
     */
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> reserveStock(
            @Valid @RequestBody InventoryDto.StockReservationDto reservationDto) {
        try {
            log.info("재고 예약 요청 - 재고 ID: {}, 수량: {}", reservationDto.inventoryId(), reservationDto.reservationQuantity());
            
            InventoryDto.InventoryResponseDto result = inventoryService.reserveStock(reservationDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 예약이 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 예약 실패 - 재고 ID: {}", reservationDto.inventoryId(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 예약에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 예약 해제
     */
    @PostMapping("/{id}/unreserve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> unreserveStock(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestParam(required = false) String reason) {
        try {
            log.info("재고 예약 해제 요청 - 재고 ID: {}, 수량: {}", id, quantity);
            
            InventoryDto.InventoryResponseDto result = inventoryService.unreserveStock(id, quantity, reason);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 예약 해제가 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 예약 해제 실패 - 재고 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 예약 해제에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 실사 처리
     */
    @PostMapping("/stocktaking")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.StocktakingResultDto>> performStocktaking(
            @Valid @RequestBody InventoryDto.StocktakingRequestDto requestDto) {
        try {
            log.info("재고 실사 처리 요청 - 재고 ID: {}, 실사 수량: {}", requestDto.inventoryId(), requestDto.actualQuantity());
            
            InventoryDto.StocktakingResultDto result = inventoryService.performStocktaking(requestDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 실사가 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 실사 처리 실패 - 재고 ID: {}", requestDto.inventoryId(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 실사 처리에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 조정
     */
    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> adjustInventory(
            @Valid @RequestBody InventoryDto.InventoryAdjustmentDto adjustmentDto) {
        try {
            log.info("재고 조정 요청 - 재고 ID: {}, 조정 수량: {}", adjustmentDto.inventoryId(), adjustmentDto.adjustmentQuantity());
            
            InventoryDto.InventoryResponseDto result = inventoryService.adjustInventory(adjustmentDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 조정이 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 조정 실패 - 재고 ID: {}", adjustmentDto.inventoryId(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 조정에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 이동 (창고간)
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventoryResponseDto>>> transferInventory(
            @Valid @RequestBody InventoryDto.InventoryTransferDto transferDto) {
        try {
            log.info("재고 이동 요청 - 출발지 재고 ID: {}, 도착지 창고 ID: {}", transferDto.fromInventoryId(), transferDto.toWarehouseId());
            
            List<InventoryDto.InventoryResponseDto> result = inventoryService.transferInventory(transferDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 이동이 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 이동 실패 - 출발지 재고 ID: {}", transferDto.fromInventoryId(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 이동에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 위치 이동
     */
    @PostMapping("/{id}/move-location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryResponseDto>> moveInventoryLocation(
            @PathVariable Long id,
            @RequestParam String newLocationCode,
            @RequestParam(required = false) String newLocationDescription) {
        try {
            log.info("재고 위치 이동 요청 - 재고 ID: {}, 새 위치: {}", id, newLocationCode);
            
            InventoryDto.InventoryResponseDto result = inventoryService.moveInventoryLocation(id, newLocationCode, newLocationDescription);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 위치 이동이 성공적으로 처리되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 위치 이동 실패 - 재고 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 위치 이동에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 안전재고 미달 재고 조회
     */
    @GetMapping("/companies/{companyId}/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventorySummaryDto>>> getLowStockInventories(@PathVariable Long companyId) {
        try {
            log.info("안전재고 미달 재고 조회 요청 - 회사: {}", companyId);
            
            List<InventoryDto.InventorySummaryDto> result = inventoryService.getLowStockInventories(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "안전재고 미달 재고를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("안전재고 미달 재고 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("안전재고 미달 재고 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고없음 재고 조회
     */
    @GetMapping("/companies/{companyId}/out-of-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventorySummaryDto>>> getOutOfStockInventories(@PathVariable Long companyId) {
        try {
            log.info("재고없음 재고 조회 요청 - 회사: {}", companyId);
            
            List<InventoryDto.InventorySummaryDto> result = inventoryService.getOutOfStockInventories(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고없음 재고를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고없음 재고 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고없음 재고 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 통계 조회
     */
    @GetMapping("/companies/{companyId}/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDto.InventoryStatsDto>> getInventoryStats(@PathVariable Long companyId) {
        try {
            log.info("재고 통계 조회 요청 - 회사: {}", companyId);
            
            InventoryDto.InventoryStatsDto result = inventoryService.getInventoryStats(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 통계를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 통계 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 통계 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * ABC 분석
     */
    @GetMapping("/companies/{companyId}/abc-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.ABCAnalysisDto>>> performAbcAnalysis(@PathVariable Long companyId) {
        try {
            log.info("ABC 분석 요청 - 회사: {}", companyId);
            
            List<InventoryDto.ABCAnalysisDto> result = inventoryService.performAbcAnalysis(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "ABC 분석이 성공적으로 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("ABC 분석 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("ABC 분석에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 알림 대상 조회
     */
    @GetMapping("/companies/{companyId}/alerts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryDto.InventorySummaryDto>>> getInventoryAlerts(@PathVariable Long companyId) {
        try {
            log.info("재고 알림 대상 조회 요청 - 회사: {}", companyId);
            
            List<InventoryDto.InventorySummaryDto> result = inventoryService.getInventoryAlerts(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 알림 대상을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 알림 대상 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 알림 대상 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 상태 업데이트 (배치)
     */
    @PostMapping("/companies/{companyId}/update-statuses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateInventoryStatuses(@PathVariable Long companyId) {
        try {
            log.info("재고 상태 업데이트 요청 - 회사: {}", companyId);
            
            inventoryService.updateInventoryStatuses(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 상태가 성공적으로 업데이트되었습니다"
            ));
        } catch (Exception e) {
            log.error("재고 상태 업데이트 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 상태 업데이트에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 가치 업데이트 (배치)
     */
    @PostMapping("/companies/{companyId}/update-values")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateInventoryValues(@PathVariable Long companyId) {
        try {
            log.info("재고 가치 업데이트 요청 - 회사: {}", companyId);
            
            inventoryService.updateInventoryValues(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 가치가 성공적으로 업데이트되었습니다"
            ));
        } catch (Exception e) {
            log.error("재고 가치 업데이트 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 가치 업데이트에 실패했습니다: " + e.getMessage())
            );
        }
    }
}
