package com.erp.inventory.service;

import com.erp.inventory.dto.InventoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 재고 서비스 인터페이스 (기본 구현)
 * 재고 관련 기본적인 비즈니스 로직을 정의합니다
 */
public interface InventoryService {

    /**
     * 재고 생성
     */
    InventoryDto.InventoryResponseDto createInventory(InventoryDto.InventoryCreateDto createDto);

    /**
     * 재고 수정
     */
    InventoryDto.InventoryResponseDto updateInventory(Long inventoryId, InventoryDto.InventoryUpdateDto updateDto);

    /**
     * 재고 삭제
     */
    void deleteInventory(Long inventoryId);

    /**
     * 재고 조회
     */
    InventoryDto.InventoryResponseDto getInventory(Long inventoryId);

    /**
     * 전체 재고 목록 조회
     */
    Page<InventoryDto.InventoryResponseDto> getAllInventories(Pageable pageable);

    /**
     * 상품별 재고 목록 조회
     */
    Page<InventoryDto.InventoryResponseDto> getInventoriesByProduct(Long productId, Pageable pageable);

    /**
     * 창고별 재고 목록 조회
     */
    Page<InventoryDto.InventorySummaryDto> getInventoriesByWarehouse(Long warehouseId, Pageable pageable);

    /**
     * 회사별 재고 목록 조회
     */
    Page<InventoryDto.InventorySummaryDto> getInventoriesByCompany(Long companyId, Pageable pageable);

    /**
     * 재고 검색
     */
    Page<InventoryDto.InventoryResponseDto> searchInventories(InventoryDto.InventorySearchDto searchDto, Pageable pageable);
    
    /**
     * 재고 검색 (간단한 시그니처)
     */
    Page<InventoryDto.InventorySummaryDto> searchInventories(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 재고 부족 목록 조회
     */
    List<InventoryDto.InventorySummaryDto> getLowStockInventories(Long companyId);

    /**
     * 재고 없음 목록 조회
     */
    List<InventoryDto.InventorySummaryDto> getOutOfStockInventories(Long companyId);

    /**
     * 유통기한 임박 재고 조회
     */
    List<InventoryDto.InventoryResponseDto> getExpiringInventories(Long companyId, int days);

    /**
     * 입고 처리
     */
    InventoryDto.InventoryResponseDto stockIn(Long inventoryId, InventoryDto.StockInDto stockInDto);

    /**
     * 출고 처리
     */
    InventoryDto.InventoryResponseDto stockOut(Long inventoryId, InventoryDto.StockOutDto stockOutDto);

    /**
     * 재고 조정
     */
    InventoryDto.InventoryResponseDto adjustStock(Long inventoryId, InventoryDto.StockAdjustmentDto adjustmentDto);

    /**
     * 재고 이동
     */
    InventoryDto.InventoryResponseDto transferStock(Long fromInventoryId, Long toWarehouseId, InventoryDto.StockTransferDto transferDto);

    /**
     * 실사 진행
     */
    List<InventoryDto.PhysicalInventoryResultDto> conductPhysicalInventory(Long companyId, InventoryDto.PhysicalInventoryDto physicalInventoryDto);

    /**
     * 실사 결과 적용
     */
    void applyPhysicalInventoryResults(List<InventoryDto.PhysicalInventoryResultDto> results, String reason);

    /**
     * 현재 재고량 조회
     */
    BigDecimal getCurrentStock(Long productId, Long warehouseId);

    /**
     * 가용 재고량 조회
     */
    BigDecimal getAvailableStock(Long productId, Long warehouseId);

    /**
     * 예약 재고량 조회
     */
    BigDecimal getReservedStock(Long productId, Long warehouseId);

    /**
     * 재고 통계 조회
     */
    InventoryDto.InventoryStatsDto getInventoryStatistics(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * ABC 분석
     */
    List<InventoryDto.ABCAnalysisDto> performABCAnalysis(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 장기 체류 재고 조회
     */
    List<InventoryDto.SlowMovingInventoryDto> getSlowMovingInventory(Long companyId, int days);

    /**
     * 창고별 재고 수 통계
     */
    List<Object[]> getInventoryCountByWarehouse();

    /**
     * 상품별 재고 수 통계
     */
    List<Object[]> getInventoryCountByProduct();

    /**
     * 창고별 재고 가치 통계
     */
    List<Object[]> getInventoryValueByWarehouse();

    /**
     * 재고 이동 트렌드
     */
    List<Object[]> getStockMovementTrends(LocalDate startDate, LocalDate endDate);

    // ==================== InventoryController에서 사용하는 추가 메서드들 ====================
    
    /**
     * ID로 재고 조회
     */
    InventoryDto.InventoryResponseDto getInventoryById(Long id);
    
    /**
     * 상품별 재고 목록 조회
     */
    List<InventoryDto.InventoryResponseDto> getInventoriesByProduct(Long productId);
    
    /**
     * 입고 처리 (간단한 시그니처)
     */
    InventoryDto.InventoryResponseDto receiveStock(Long inventoryId, Double quantity, java.math.BigDecimal unitCost, String remarks);
    
    /**
     * 출고 처리 (간단한 시그니처)
     */
    InventoryDto.InventoryResponseDto issueStock(Long inventoryId, Double quantity, String remarks);
    
    /**
     * 재고 예약
     */
    InventoryDto.InventoryResponseDto reserveStock(InventoryDto.StockReservationDto reservationDto);
    
    /**
     * 재고 예약 해제
     */
    InventoryDto.InventoryResponseDto unreserveStock(Long inventoryId, Double quantity, String remarks);
    
    /**
     * 재고 실사
     */
    InventoryDto.StocktakingResultDto performStocktaking(InventoryDto.StocktakingRequestDto requestDto);
    
    /**
     * 재고 조정 (다른 시그니처)
     */
    InventoryDto.InventoryResponseDto adjustInventory(InventoryDto.InventoryAdjustmentDto adjustmentDto);
    
    /**
     * 재고 이동
     */
    List<InventoryDto.InventoryResponseDto> transferInventory(InventoryDto.InventoryTransferDto transferDto);
    
    /**
     * 재고 위치 이동
     */
    InventoryDto.InventoryResponseDto moveInventoryLocation(Long inventoryId, String fromLocation, String toLocation);
    
    /**
     * 재고 통계
     */
    InventoryDto.InventoryStatsDto getInventoryStats(Long companyId);
    
    /**
     * ABC 분석 수행
     */
    List<InventoryDto.ABCAnalysisDto> performAbcAnalysis(Long companyId);
    
    /**
     * 재고 알림 조회
     */
    List<InventoryDto.InventorySummaryDto> getInventoryAlerts(Long companyId);
    
    /**
     * 재고 상태 업데이트
     */
    void updateInventoryStatuses(Long companyId);
    
    /**
     * 재고 가치 업데이트 (중복 메서드)
     */
    void updateInventoryValues(Long companyId);
}