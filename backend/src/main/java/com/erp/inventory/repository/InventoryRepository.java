package com.erp.inventory.repository;

import com.erp.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 재고 Repository
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * 회사별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Page<Inventory> findByCompanyIdOrderByLastStockUpdateDesc(Long companyId, Pageable pageable);

    /**
     * 상품별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByProductIdOrderByWarehouseWarehouseNameAscLocationCodeAsc(Long productId);

    /**
     * 창고별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Page<Inventory> findByWarehouseIdOrderByProductProductNameAsc(Long warehouseId, Pageable pageable);

    /**
     * 상품-창고-위치별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Optional<Inventory> findByProductIdAndWarehouseIdAndLocationCode(Long productId, Long warehouseId, String locationCode);

    /**
     * 재고 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND (i.product.productName LIKE %:searchTerm% OR i.product.productCode LIKE %:searchTerm% " +
           "OR i.warehouse.warehouseName LIKE %:searchTerm% OR i.locationCode LIKE %:searchTerm%)")
    Page<Inventory> searchInventory(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 재고 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND (:categoryId IS NULL OR i.product.category.id = :categoryId) " +
           "AND (:warehouseId IS NULL OR i.warehouse.id = :warehouseId) " +
           "AND (:stockStatus IS NULL OR i.stockStatus = :stockStatus) " +
           "AND (:stockGrade IS NULL OR i.stockGrade = :stockGrade) " +
           "AND (:isLowStock IS NULL OR i.isLowStock = :isLowStock) " +
           "AND (:isOutOfStock IS NULL OR i.isOutOfStock = :isOutOfStock) " +
           "AND (:isOverStock IS NULL OR i.isOverStock = :isOverStock) " +
           "AND (:needsReorder IS NULL OR i.needsReorder = :needsReorder) " +
           "AND (:lotNumber IS NULL OR i.lotNumber LIKE %:lotNumber%) " +
           "AND (:serialNumber IS NULL OR i.serialNumber LIKE %:serialNumber%)")
    Page<Inventory> searchInventoryAdvanced(
            @Param("companyId") Long companyId,
            @Param("categoryId") Long categoryId,
            @Param("warehouseId") Long warehouseId,
            @Param("stockStatus") Inventory.StockStatus stockStatus,
            @Param("stockGrade") Inventory.StockGrade stockGrade,
            @Param("isLowStock") Boolean isLowStock,
            @Param("isOutOfStock") Boolean isOutOfStock,
            @Param("isOverStock") Boolean isOverStock,
            @Param("needsReorder") Boolean needsReorder,
            @Param("lotNumber") String lotNumber,
            @Param("serialNumber") String serialNumber,
            Pageable pageable
    );

    /**
     * 안전재고 미달 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdAndIsLowStockTrueOrderByLastStockUpdateDesc(Long companyId);

    /**
     * 재고없음 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdAndIsOutOfStockTrueOrderByLastStockUpdateDesc(Long companyId);

    /**
     * 과재고 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdAndIsOverStockTrueOrderByLastStockUpdateDesc(Long companyId);

    /**
     * 재주문 필요 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdAndNeedsReorderTrueOrderByLastStockUpdateDesc(Long companyId);

    /**
     * 유효기간 임박 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.expiryDate IS NOT NULL " +
           "AND i.expiryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY i.expiryDate ASC")
    List<Inventory> findExpiringSoonInventory(@Param("companyId") Long companyId, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * 유효기간 만료 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.expiryDate IS NOT NULL AND i.expiryDate < :currentDate " +
           "ORDER BY i.expiryDate ASC")
    List<Inventory> findExpiredInventory(@Param("companyId") Long companyId, @Param("currentDate") LocalDateTime currentDate);

    /**
     * 로트 번호별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdAndLotNumberOrderByExpiryDateAsc(Long companyId, String lotNumber);

    /**
     * 시리얼 번호별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Optional<Inventory> findByCompanyIdAndSerialNumber(Long companyId, String serialNumber);

    /**
     * 창고별 재고 통계
     */
    @Query("SELECT new com.erp.inventory.dto.InventoryDto$WarehouseInventoryDto(" +
           "w.id, w.warehouseName, " +
           "COUNT(i), " +
           "COALESCE(SUM(i.currentStock), 0.0), " +
           "COALESCE(SUM(i.totalStockValue), 0), " +
           "COUNT(CASE WHEN i.isLowStock = true THEN 1 END), " +
           "COUNT(CASE WHEN i.isOutOfStock = true THEN 1 END), " +
           "COALESCE(AVG(CASE WHEN w.maxCapacity > 0 THEN i.currentStock / w.maxCapacity * 100 END), 0.0), " +
           "null" +
           ") " +
           "FROM Inventory i JOIN i.warehouse w WHERE i.company.id = :companyId " +
           "GROUP BY w.id, w.warehouseName")
    List<com.erp.inventory.dto.InventoryDto.WarehouseInventoryDto> getWarehouseInventoryStats(@Param("companyId") Long companyId);

    /**
     * 재고 통계 조회
     */
    @Query("SELECT new com.erp.inventory.dto.InventoryDto$InventoryStatsDto(" +
           "COUNT(i), " +
           "COUNT(CASE WHEN i.isLowStock = true THEN 1 END), " +
           "COUNT(CASE WHEN i.isOutOfStock = true THEN 1 END), " +
           "COUNT(CASE WHEN i.isOverStock = true THEN 1 END), " +
           "COUNT(CASE WHEN i.needsReorder = true THEN 1 END), " +
           "COUNT(CASE WHEN i.expiryDate IS NOT NULL AND i.expiryDate BETWEEN :now AND :futureDate THEN 1 END), " +
           "COUNT(CASE WHEN i.expiryDate IS NOT NULL AND i.expiryDate < :now THEN 1 END), " +
           "COALESCE(SUM(i.totalStockValue), 0), " +
           "COALESCE(SUM(CASE WHEN i.isLowStock = true THEN i.totalStockValue END), 0), " +
           "COALESCE(AVG(i.totalStockValue), 0), " +
           "0.0" +
           ") " +
           "FROM Inventory i WHERE i.company.id = :companyId")
    com.erp.inventory.dto.InventoryDto.InventoryStatsDto getInventoryStats(
            @Param("companyId") Long companyId, 
            @Param("now") LocalDateTime now, 
            @Param("futureDate") LocalDateTime futureDate);

    /**
     * 상품별 총 재고 수량 조회
     */
    @Query("SELECT i.product.id, COALESCE(SUM(i.currentStock), 0.0) FROM Inventory i " +
           "WHERE i.product.id IN :productIds GROUP BY i.product.id")
    List<Object[]> getTotalStockByProducts(@Param("productIds") List<Long> productIds);

    /**
     * 상품별 사용 가능한 재고 수량 조회
     */
    @Query("SELECT i.product.id, COALESCE(SUM(i.availableStock), 0.0) FROM Inventory i " +
           "WHERE i.product.id IN :productIds GROUP BY i.product.id")
    List<Object[]> getAvailableStockByProducts(@Param("productIds") List<Long> productIds);

    /**
     * 재고 입고 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.currentStock = i.currentStock + :quantity, " +
           "i.availableStock = i.availableStock + :quantity, " +
           "i.lastReceiptDate = :receiptDate, " +
           "i.movementCount = i.movementCount + 1, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void receiveStock(@Param("inventoryId") Long inventoryId, 
                     @Param("quantity") Double quantity, 
                     @Param("receiptDate") LocalDateTime receiptDate,
                     @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 출고 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.currentStock = i.currentStock - :quantity, " +
           "i.availableStock = i.availableStock - :quantity, " +
           "i.lastIssueDate = :issueDate, " +
           "i.movementCount = i.movementCount + 1, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId AND i.availableStock >= :quantity")
    int issueStock(@Param("inventoryId") Long inventoryId, 
                   @Param("quantity") Double quantity, 
                   @Param("issueDate") LocalDateTime issueDate,
                   @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 예약 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.reservedStock = i.reservedStock + :quantity, " +
           "i.availableStock = i.availableStock - :quantity, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId AND i.availableStock >= :quantity")
    int reserveStock(@Param("inventoryId") Long inventoryId, 
                     @Param("quantity") Double quantity,
                     @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 예약 해제 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.reservedStock = GREATEST(0, i.reservedStock - :quantity), " +
           "i.availableStock = i.availableStock + LEAST(i.reservedStock, :quantity), " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void unreserveStock(@Param("inventoryId") Long inventoryId, 
                        @Param("quantity") Double quantity,
                        @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 실사 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.currentStock = :actualQuantity, " +
           "i.lastStocktakingDate = :stocktakingDate, " +
           "i.movementCount = i.movementCount + 1, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void adjustStockByStocktaking(@Param("inventoryId") Long inventoryId, 
                                  @Param("actualQuantity") Double actualQuantity,
                                  @Param("stocktakingDate") LocalDateTime stocktakingDate,
                                  @Param("updateTime") LocalDateTime updateTime);

    /**
     * 평균 원가 업데이트
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.averageCost = :averageCost, i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void updateAverageCost(@Param("inventoryId") Long inventoryId, 
                          @Param("averageCost") BigDecimal averageCost,
                          @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 가치 업데이트
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.totalStockValue = i.currentStock * i.averageCost, " +
           "i.lastStockUpdate = :updateTime WHERE i.id = :inventoryId")
    void updateStockValue(@Param("inventoryId") Long inventoryId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 회사의 모든 재고 가치 업데이트
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.totalStockValue = i.currentStock * i.averageCost " +
           "WHERE i.company.id = :companyId")
    void updateAllStockValues(@Param("companyId") Long companyId);

    /**
     * 재고 상태 업데이트
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.stockStatus = :stockStatus, " +
           "i.isLowStock = :isLowStock, " +
           "i.isOutOfStock = :isOutOfStock, " +
           "i.isOverStock = :isOverStock, " +
           "i.needsReorder = :needsReorder, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void updateStockStatus(@Param("inventoryId") Long inventoryId,
                          @Param("stockStatus") Inventory.StockStatus stockStatus,
                          @Param("isLowStock") Boolean isLowStock,
                          @Param("isOutOfStock") Boolean isOutOfStock,
                          @Param("isOverStock") Boolean isOverStock,
                          @Param("needsReorder") Boolean needsReorder,
                          @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 위치 이동
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.locationCode = :newLocationCode, " +
           "i.locationDescription = :newLocationDescription, " +
           "i.lastStockUpdate = :updateTime " +
           "WHERE i.id = :inventoryId")
    void moveInventoryLocation(@Param("inventoryId") Long inventoryId,
                              @Param("newLocationCode") String newLocationCode,
                              @Param("newLocationDescription") String newLocationDescription,
                              @Param("updateTime") LocalDateTime updateTime);

    /**
     * 재고 가치 상위 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.currentStock > 0 ORDER BY i.totalStockValue DESC")
    List<Inventory> findTopValueInventory(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 재고 수량 상위 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.currentStock > 0 ORDER BY i.currentStock DESC")
    List<Inventory> findTopQuantityInventory(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 장기 재고 조회 (90일 이상 입출고 없음)
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.currentStock > 0 " +
           "AND (i.lastIssueDate IS NULL OR i.lastIssueDate < :cutoffDate) " +
           "AND (i.lastReceiptDate IS NULL OR i.lastReceiptDate < :cutoffDate) " +
           "ORDER BY COALESCE(i.lastIssueDate, i.lastReceiptDate, i.createdAt) ASC")
    List<Inventory> findSlowMovingInventory(@Param("companyId") Long companyId, 
                                          @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 재고 회전율 분석
     */
    @Query("SELECT i.product.id, i.product.productCode, i.product.productName, " +
           "i.currentStock, i.totalStockValue, " +
           "COALESCE(sm.totalIssued, 0.0) as totalIssued, " +
           "CASE WHEN i.currentStock > 0 THEN COALESCE(sm.totalIssued, 0.0) / i.currentStock ELSE 0 END as turnoverRate " +
           "FROM Inventory i " +
           "LEFT JOIN (" +
           "  SELECT sm.product.id as productId, SUM(sm.quantity) as totalIssued " +
           "  FROM StockMovement sm " +
           "  WHERE sm.company.id = :companyId " +
           "  AND sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE') " +
           "  AND sm.movementDate >= :startDate " +
           "  AND sm.movementStatus = 'PROCESSED' " +
           "  GROUP BY sm.product.id" +
           ") sm ON i.product.id = sm.productId " +
           "WHERE i.company.id = :companyId AND i.currentStock > 0 " +
           "ORDER BY turnoverRate DESC")
    List<Object[]> getInventoryTurnoverAnalysis(@Param("companyId") Long companyId, 
                                               @Param("startDate") LocalDateTime startDate);

    /**
     * 중복 재고 확인 (동일 상품-창고-위치)
     */
    boolean existsByProductIdAndWarehouseIdAndLocationCodeAndIdNot(Long productId, Long warehouseId, String locationCode, Long id);

    /**
     * 회사별 재고 수 조회
     */
    Long countByCompanyId(Long companyId);

    /**
     * 창고별 재고 수 조회
     */
    Long countByWarehouseId(Long warehouseId);

    /**
     * 상품별 재고 수 조회
     */
    Long countByProductId(Long productId);

    // ==================== Dashboard용 메서드들 ====================
    
    /**
     * 재고 부족 상품 수
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.currentStock <= i.minStock AND i.minStock > 0")
    long countLowStockProducts(@Param("companyId") Long companyId);
    
    /**
     * 전체 재고 가치
     */
    @Query("SELECT COALESCE(SUM(i.totalStockValue), 0) FROM Inventory i WHERE i.company.id = :companyId")
    BigDecimal getTotalInventoryValue(@Param("companyId") Long companyId);
}
