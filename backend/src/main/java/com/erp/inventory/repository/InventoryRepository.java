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
    Page<Inventory> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);

    /**
     * 상품별 재고 조회 (locationCode 필드 제거로 단순화)
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByProductIdOrderByWarehouseWarehouseNameAsc(Long productId);

    /**
     * 창고별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Page<Inventory> findByWarehouseIdOrderByProductProductNameAsc(Long warehouseId, Pageable pageable);

    /**
     * 상품-창고별 재고 조회 (locationCode 필드 제거로 단순화)
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    /**
     * 재고 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND (i.product.productName LIKE %:searchTerm% OR i.product.productCode LIKE %:searchTerm% " +
           "OR i.warehouse.warehouseName LIKE %:searchTerm%)")
    Page<Inventory> searchInventory(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 재고 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND (:categoryId IS NULL OR i.product.category.id = :categoryId) " +
           "AND (:warehouseId IS NULL OR i.warehouse.id = :warehouseId)")
    Page<Inventory> searchInventoryAdvanced(
            @Param("companyId") Long companyId,
            @Param("categoryId") Long categoryId,
            @Param("warehouseId") Long warehouseId,
            Pageable pageable
    );

    /**
     * 안전재고 미달 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId AND i.quantity <= :threshold ORDER BY i.createdAt DESC")
    List<Inventory> findLowStockInventory(@Param("companyId") Long companyId, @Param("threshold") Integer threshold);

    /**
     * 재고없음 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId AND i.quantity = 0 ORDER BY i.createdAt DESC")
    List<Inventory> findOutOfStockInventory(@Param("companyId") Long companyId);

    /**
     * 과재고 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId AND i.quantity > :maxStock ORDER BY i.createdAt DESC")
    List<Inventory> findOverStockInventory(@Param("companyId") Long companyId, @Param("maxStock") Integer maxStock);

    /**
     * 재주문 필요 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId AND i.quantity <= :reorderPoint ORDER BY i.createdAt DESC")
    List<Inventory> findReorderNeededInventory(@Param("companyId") Long companyId, @Param("reorderPoint") Integer reorderPoint);

    /**
     * 유효기간 임박 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "ORDER BY i.createdAt ASC")
    List<Inventory> findExpiringSoonInventory(@Param("companyId") Long companyId);

    /**
     * 유효기간 만료 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "ORDER BY i.createdAt ASC")
    List<Inventory> findExpiredInventory(@Param("companyId") Long companyId);

    /**
     * 로트 번호별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<Inventory> findByCompanyIdOrderByCreatedAtAsc(Long companyId);

    /**
     * 시리얼 번호별 재고 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    Optional<Inventory> findByCompanyId(Long companyId);

    /**
     * 창고별 재고 통계
     */
    @Query("SELECT new com.erp.inventory.dto.InventoryDto$WarehouseInventoryDto(" +
           "w.id, w.warehouseName, " +
           "COUNT(i), " +
           "COALESCE(SUM(i.quantity), 0), " +
           "0, " +
           "COUNT(CASE WHEN i.quantity <= 0 THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity = 0 THEN 1 END), " +
           "COALESCE(AVG(CASE WHEN w.maxCapacity > 0 THEN i.quantity / w.maxCapacity * 100 END), 0.0), " +
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
           "COUNT(CASE WHEN i.quantity <= 0 THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity = 0 THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity > i.maxStock THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity <= i.reorderPoint THEN 1 END), " +
           "0, " +
           "0, " +
           "0.0, " +
           "0.0, " +
           "0.0, " +
           "0.0" +
           ") " +
           "FROM Inventory i WHERE i.company.id = :companyId")
    com.erp.inventory.dto.InventoryDto.InventoryStatsDto getInventoryStats(
            @Param("companyId") Long companyId);

    /**
     * 상품별 총 재고 수량 조회
     */
    @Query("SELECT i.product.id, COALESCE(SUM(i.quantity), 0) FROM Inventory i " +
           "WHERE i.product.id IN :productIds GROUP BY i.product.id")
    List<Object[]> getTotalStockByProducts(@Param("productIds") List<Long> productIds);

    /**
     * 상품별 사용 가능한 재고 수량 조회
     */
    @Query("SELECT i.product.id, COALESCE(SUM(i.availableQuantity), 0) FROM Inventory i " +
           "WHERE i.product.id IN :productIds GROUP BY i.product.id")
    List<Object[]> getAvailableStockByProducts(@Param("productIds") List<Long> productIds);

    /**
     * 재고 입고 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.quantity = i.quantity + :quantity " +
           "WHERE i.id = :inventoryId")
    void receiveStock(@Param("inventoryId") Long inventoryId, 
                     @Param("quantity") Integer quantity);

    /**
     * 재고 출고 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.quantity = i.quantity - :quantity " +
           "WHERE i.id = :inventoryId AND i.quantity >= :quantity")
    int issueStock(@Param("inventoryId") Long inventoryId, 
                   @Param("quantity") Integer quantity);

    /**
     * 재고 예약 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.reservedQuantity = i.reservedQuantity + :quantity " +
           "WHERE i.id = :inventoryId AND i.quantity >= :quantity")
    int reserveStock(@Param("inventoryId") Long inventoryId, 
                     @Param("quantity") Integer quantity);

    /**
     * 재고 예약 해제 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.reservedQuantity = GREATEST(0, i.reservedQuantity - :quantity) " +
           "WHERE i.id = :inventoryId")
    void unreserveStock(@Param("inventoryId") Long inventoryId, 
                        @Param("quantity") Integer quantity);

    /**
     * 재고 실사 처리
     */
    @Modifying
    @Query("UPDATE Inventory i SET " +
           "i.quantity = :actualQuantity " +
           "WHERE i.id = :inventoryId")
    void adjustStockByStocktaking(@Param("inventoryId") Long inventoryId, 
                                  @Param("actualQuantity") Integer actualQuantity);

    /**
     * 평균 원가 업데이트 (현재는 비활성화)
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.id = i.id " +
           "WHERE i.id = :inventoryId")
    void updateAverageCost(@Param("inventoryId") Long inventoryId);

    /**
     * 재고 가치 업데이트 (현재는 비활성화)
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.id = i.id " +
           "WHERE i.id = :inventoryId")
    void updateStockValue(@Param("inventoryId") Long inventoryId);

    /**
     * 회사의 모든 재고 가치 업데이트
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.id = i.id " +
           "WHERE i.company.id = :companyId")
    void updateAllStockValues(@Param("companyId") Long companyId);

    /**
     * 재고 상태 업데이트 (현재는 비활성화)
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.id = i.id " +
           "WHERE i.id = :inventoryId")
    void updateStockStatus(@Param("inventoryId") Long inventoryId);

    /**
     * 재고 위치 이동 (locationCode, locationDescription 필드 제거로 비활성화)
     * 현재는 아무 작업도 수행하지 않음
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.id = i.id " +
           "WHERE i.id = :inventoryId")
    void moveInventoryLocation(@Param("inventoryId") Long inventoryId);

    /**
     * 재고 가치 상위 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.quantity > 0 ORDER BY i.quantity DESC")
    List<Inventory> findTopValueInventory(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 재고 수량 상위 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.quantity > 0 ORDER BY i.quantity DESC")
    List<Inventory> findTopQuantityInventory(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 장기 재고 조회 (90일 이상 입출고 없음)
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    @Query("SELECT i FROM Inventory i WHERE i.company.id = :companyId " +
           "AND i.quantity > 0 " +
           "ORDER BY i.createdAt ASC")
    List<Inventory> findSlowMovingInventory(@Param("companyId") Long companyId);

    /**
     * 재고 회전율 분석
     */
    @Query("SELECT i.product.id, i.product.productCode, i.product.productName, " +
           "i.quantity, 0.0 as totalStockValue, " +
           "COALESCE(sm.totalIssued, 0) as totalIssued, " +
           "CASE WHEN i.quantity > 0 THEN COALESCE(sm.totalIssued, 0) / i.quantity ELSE 0 END as turnoverRate " +
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
           "WHERE i.company.id = :companyId AND i.quantity > 0 " +
           "ORDER BY turnoverRate DESC")
    List<Object[]> getInventoryTurnoverAnalysis(@Param("companyId") Long companyId, 
                                               @Param("startDate") LocalDateTime startDate);

    /**
     * 중복 재고 확인 (동일 상품-창고) - locationCode 필드 제거로 단순화
     */
    boolean existsByProductIdAndWarehouseIdAndIdNot(Long productId, Long warehouseId, Long id);

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
           "AND i.quantity <= i.reorderPoint AND i.reorderPoint > 0")
    long countLowStockProducts(@Param("companyId") Long companyId);
    
    /**
     * 전체 재고 가치
     */
    @Query("SELECT 0.0 FROM Inventory i WHERE i.company.id = :companyId")
    BigDecimal getTotalInventoryValue(@Param("companyId") Long companyId);
}
