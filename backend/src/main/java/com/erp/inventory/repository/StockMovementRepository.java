package com.erp.inventory.repository;

import com.erp.inventory.entity.StockMovement;
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
 * 재고이동 Repository
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /**
     * 회사별 재고이동 조회 (최신순)
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    Page<StockMovement> findByCompanyIdOrderByMovementDateDesc(Long companyId, Pageable pageable);

    /**
     * 상품별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    Page<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId, Pageable pageable);

    /**
     * 창고별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    Page<StockMovement> findByWarehouseIdOrderByMovementDateDesc(Long warehouseId, Pageable pageable);

    /**
     * 이동 번호로 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    Optional<StockMovement> findByCompanyIdAndMovementNumber(Long companyId, String movementNumber);

    /**
     * 재고이동 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND (sm.movementNumber LIKE %:searchTerm% OR sm.product.productName LIKE %:searchTerm% " +
           "OR sm.product.productCode LIKE %:searchTerm% OR sm.warehouse.warehouseName LIKE %:searchTerm% " +
           "OR sm.businessPartner LIKE %:searchTerm% OR sm.referenceNumber LIKE %:searchTerm%)")
    Page<StockMovement> searchMovements(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 재고이동 검색
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy", "approvedBy"})
    @Query("SELECT sm FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND (:productId IS NULL OR sm.product.id = :productId) " +
           "AND (:warehouseId IS NULL OR sm.warehouse.id = :warehouseId) " +
           "AND (:movementType IS NULL OR sm.movementType = :movementType) " +
           "AND (:movementStatus IS NULL OR sm.movementStatus = :movementStatus) " +
           "AND (:startDate IS NULL OR sm.movementDate >= :startDate) " +
           "AND (:endDate IS NULL OR sm.movementDate <= :endDate) " +
           "AND (:referenceNumber IS NULL OR sm.referenceNumber LIKE %:referenceNumber%) " +
           "AND (:businessPartner IS NULL OR sm.businessPartner LIKE %:businessPartner%) " +
           "AND (:lotNumber IS NULL OR sm.lotNumber LIKE %:lotNumber%) " +
           "AND (:serialNumber IS NULL OR sm.serialNumber LIKE %:serialNumber%)")
    Page<StockMovement> searchMovementsAdvanced(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("movementType") StockMovement.MovementType movementType,
            @Param("movementStatus") StockMovement.MovementStatus movementStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("referenceNumber") String referenceNumber,
            @Param("businessPartner") String businessPartner,
            @Param("lotNumber") String lotNumber,
            @Param("serialNumber") String serialNumber,
            Pageable pageable
    );

    /**
     * 이동 유형별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<StockMovement> findByCompanyIdAndMovementTypeOrderByMovementDateDesc(Long companyId, StockMovement.MovementType movementType);

    /**
     * 이동 상태별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<StockMovement> findByCompanyIdAndMovementStatusOrderByMovementDateDesc(Long companyId, StockMovement.MovementStatus movementStatus);

    /**
     * 승인 대기 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse", "processedBy"})
    List<StockMovement> findByCompanyIdAndMovementStatusOrderByCreatedAtAsc(Long companyId, StockMovement.MovementStatus movementStatus);

    /**
     * 참조 번호별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<StockMovement> findByCompanyIdAndReferenceNumberOrderByMovementDateDesc(Long companyId, String referenceNumber);

    /**
     * 로트 번호별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<StockMovement> findByCompanyIdAndLotNumberOrderByMovementDateDesc(Long companyId, String lotNumber);

    /**
     * 시리얼 번호별 재고이동 조회
     */
    @EntityGraph(attributePaths = {"company", "product", "product.category", "warehouse"})
    List<StockMovement> findByCompanyIdAndSerialNumberOrderByMovementDateDesc(Long companyId, String serialNumber);

    /**
     * 기간별 재고이동 통계
     */
    @Query("SELECT new com.erp.inventory.dto.StockMovementDto$MovementStatsDto(" +
           ":startDate, :endDate, " +
           "COUNT(sm), " +
           "COUNT(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN', 'STOCKTAKING_INCREASE') THEN 1 END), " +
           "COUNT(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL', 'STOCKTAKING_DECREASE') THEN 1 END), " +
           "COUNT(CASE WHEN sm.movementType IN ('WAREHOUSE_TRANSFER', 'LOCATION_TRANSFER') THEN 1 END), " +
           "COALESCE(SUM(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN', 'STOCKTAKING_INCREASE') THEN sm.quantity END), 0.0), " +
           "COALESCE(SUM(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL', 'STOCKTAKING_DECREASE') THEN sm.quantity END), 0.0), " +
           "COALESCE(SUM(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN', 'STOCKTAKING_INCREASE') THEN sm.totalAmount END), 0), " +
           "COALESCE(SUM(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL', 'STOCKTAKING_DECREASE') THEN sm.totalAmount END), 0), " +
           "COUNT(CASE WHEN sm.movementStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN sm.movementStatus = 'CANCELLED' OR sm.movementStatus = 'REJECTED' THEN 1 END), " +
           "null, null" +
           ") " +
           "FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "AND sm.movementStatus = 'PROCESSED'")
    com.erp.inventory.dto.StockMovementDto.MovementStatsDto getMovementStats(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 이동 유형별 통계
     */
    @Query("SELECT new com.erp.inventory.dto.StockMovementDto$MovementStatsDto$MovementTypeStatsDto(" +
           "sm.movementType, '', " +
           "COUNT(sm), " +
           "COALESCE(SUM(sm.quantity), 0.0), " +
           "COALESCE(SUM(sm.totalAmount), 0)" +
           ") " +
           "FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "AND sm.movementStatus = 'PROCESSED' " +
           "GROUP BY sm.movementType " +
           "ORDER BY COUNT(sm) DESC")
    List<com.erp.inventory.dto.StockMovementDto.MovementStatsDto.MovementTypeStatsDto> getMovementTypeStats(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 상품별 이동 통계
     */
    @Query("SELECT new com.erp.inventory.dto.StockMovementDto$MovementStatsDto$ProductMovementStatsDto(" +
           "p.id, p.productCode, p.productName, " +
           "COUNT(sm), " +
           "COALESCE(SUM(sm.quantity), 0.0), " +
           "COALESCE(SUM(sm.totalAmount), 0)" +
           ") " +
           "FROM StockMovement sm JOIN sm.product p WHERE sm.company.id = :companyId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "AND sm.movementStatus = 'PROCESSED' " +
           "GROUP BY p.id, p.productCode, p.productName " +
           "ORDER BY COUNT(sm) DESC")
    List<com.erp.inventory.dto.StockMovementDto.MovementStatsDto.ProductMovementStatsDto> getProductMovementStats(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 일별 이동 수량 조회
     * DATE() 함수 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT DATE(sm.movementDate) as movementDate, " +
    //        "COALESCE(SUM(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN', 'STOCKTAKING_INCREASE') THEN sm.quantity END), 0.0) as inboundQuantity, " +
    //        "COALESCE(SUM(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL', 'STOCKTAKING_DECREASE') THEN sm.quantity END), 0.0) as outboundQuantity " +
    //        "FROM StockMovement sm WHERE sm.company.id = :companyId " +
    //        "AND sm.movementDate BETWEEN :startDate AND :endDate " +
    //        "AND sm.movementStatus = 'PROCESSED' " +
    //        "GROUP BY DATE(sm.movementDate) " +
    //        "ORDER BY DATE(sm.movementDate)")
    // List<Object[]> getDailyMovementQuantities(@Param("companyId") Long companyId,
    //                                          @Param("startDate") LocalDateTime startDate,
    //                                          @Param("endDate") LocalDateTime endDate);

    /**
     * 월별 이동 수량 조회
     * YEAR(), MONTH() 함수 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT YEAR(sm.movementDate) as year, MONTH(sm.movementDate) as month, " +
    //        "COALESCE(SUM(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN', 'STOCKTAKING_INCREASE') THEN sm.quantity END), 0.0) as inboundQuantity, " +
    //        "COALESCE(SUM(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL', 'STOCKTAKING_DECREASE') THEN sm.quantity END), 0.0) as outboundQuantity " +
    //        "FROM StockMovement sm WHERE sm.company.id = :companyId " +
    //        "AND sm.movementDate BETWEEN :startDate AND :endDate " +
    //        "AND sm.movementStatus = 'PROCESSED' " +
    //        "GROUP BY YEAR(sm.movementDate), MONTH(sm.movementDate) " +
    //        "ORDER BY YEAR(sm.movementDate), MONTH(sm.movementDate)")
    // List<Object[]> getMonthlyMovementQuantities(@Param("companyId") Long companyId,
    //                                            @Param("startDate") LocalDateTime startDate,
    //                                            @Param("endDate") LocalDateTime endDate);

    /**
     * 재고이동 승인
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.movementStatus = 'APPROVED', " +
           "sm.approvedBy.id = :approverId, sm.approvedAt = :approvedAt " +
           "WHERE sm.id = :movementId AND sm.movementStatus = 'PENDING'")
    int approveMovement(@Param("movementId") Long movementId,
                       @Param("approverId") Long approverId,
                       @Param("approvedAt") LocalDateTime approvedAt);

    /**
     * 재고이동 처리
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.movementStatus = 'PROCESSED', " +
           "sm.processedBy.id = :processorId, sm.processedAt = :processedAt " +
           "WHERE sm.id = :movementId AND sm.movementStatus IN ('APPROVED', 'PENDING')")
    int processMovement(@Param("movementId") Long movementId,
                       @Param("processorId") Long processorId,
                       @Param("processedAt") LocalDateTime processedAt);

    /**
     * 재고이동 취소
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.movementStatus = 'CANCELLED', " +
           "sm.cancelReason = :cancelReason, sm.cancelledAt = :cancelledAt " +
           "WHERE sm.id = :movementId AND sm.movementStatus IN ('DRAFT', 'PENDING')")
    int cancelMovement(@Param("movementId") Long movementId,
                      @Param("cancelReason") String cancelReason,
                      @Param("cancelledAt") LocalDateTime cancelledAt);

    /**
     * 재고이동 반려
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.movementStatus = 'REJECTED', " +
           "sm.cancelReason = :rejectReason, sm.cancelledAt = :rejectedAt " +
           "WHERE sm.id = :movementId AND sm.movementStatus = 'PENDING'")
    int rejectMovement(@Param("movementId") Long movementId,
                      @Param("rejectReason") String rejectReason,
                      @Param("rejectedAt") LocalDateTime rejectedAt);

    /**
     * 재고 전후 수량 업데이트
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.beforeStock = :beforeStock, sm.afterStock = :afterStock " +
           "WHERE sm.id = :movementId")
    void updateStockQuantities(@Param("movementId") Long movementId,
                              @Param("beforeStock") Double beforeStock,
                              @Param("afterStock") Double afterStock);

    /**
     * 이동 번호 중복 확인
     */
    boolean existsByCompanyIdAndMovementNumberAndIdNot(Long companyId, String movementNumber, Long id);

    /**
     * 회사별 이동 수 조회
     */
    Long countByCompanyId(Long companyId);

    /**
     * 상태별 이동 수 조회
     */
    Long countByCompanyIdAndMovementStatus(Long companyId, StockMovement.MovementStatus movementStatus);

    /**
     * 오늘 이동 수 조회
     * JPQL 날짜 연산 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.company.id = :companyId " +
    //        "AND sm.movementDate >= :startDate AND sm.movementDate < :endDate")
    // Long countTodayMovements(@Param("companyId") Long companyId, 
    //                         @Param("startDate") java.time.LocalDateTime startDate,
    //                         @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 최근 이동 번호 조회 (자동 생성용)
     */
    @Query("SELECT sm.movementNumber FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND sm.movementType = :movementType " +
           "AND sm.movementNumber LIKE :prefix% " +
           "ORDER BY sm.movementNumber DESC")
    List<String> findLatestMovementNumbers(@Param("companyId") Long companyId,
                                         @Param("movementType") StockMovement.MovementType movementType,
                                         @Param("prefix") String prefix,
                                         Pageable pageable);

    /**
     * 처리자별 이동 수 조회
     */
    @Query("SELECT u.fullName, COUNT(sm) FROM StockMovement sm JOIN sm.processedBy u " +
           "WHERE sm.company.id = :companyId " +
           "AND sm.movementDate BETWEEN :startDate AND :endDate " +
           "AND sm.movementStatus = 'PROCESSED' " +
           "GROUP BY u.id, u.fullName " +
           "ORDER BY COUNT(sm) DESC")
    List<Object[]> getMovementCountByProcessor(@Param("companyId") Long companyId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * 오류가 있는 이동 조회 (차대 불일치 등)
     */
    @Query("SELECT sm FROM StockMovement sm WHERE sm.company.id = :companyId " +
           "AND sm.movementStatus = 'PROCESSED' " +
           "AND (sm.beforeStock IS NULL OR sm.afterStock IS NULL " +
           "OR (sm.beforeStock + sm.quantity != sm.afterStock AND sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT')) " +
           "OR (sm.beforeStock - sm.quantity != sm.afterStock AND sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE')))")
    List<StockMovement> findInconsistentMovements(@Param("companyId") Long companyId);

    /**
     * 대량 이동 상태 업데이트
     */
    @Modifying
    @Query("UPDATE StockMovement sm SET sm.movementStatus = :newStatus " +
           "WHERE sm.id IN :movementIds AND sm.movementStatus = :currentStatus")
    int bulkUpdateMovementStatus(@Param("movementIds") List<Long> movementIds,
                                @Param("currentStatus") StockMovement.MovementStatus currentStatus,
                                @Param("newStatus") StockMovement.MovementStatus newStatus);
}
