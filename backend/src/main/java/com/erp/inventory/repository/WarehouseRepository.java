package com.erp.inventory.repository;

import com.erp.inventory.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 창고 Repository
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /**
     * 회사별 창고 조회 (활성화된 것만)
     */
    @EntityGraph(attributePaths = {"company"})
    List<Warehouse> findByCompanyIdAndIsActiveTrueOrderBySortOrderAscWarehouseNameAsc(Long companyId);

    /**
     * 회사별 모든 창고 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Warehouse> findByCompanyIdOrderBySortOrderAscWarehouseNameAsc(Long companyId, Pageable pageable);

    /**
     * 회사 및 창고 코드로 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Optional<Warehouse> findByCompanyIdAndWarehouseCode(Long companyId, String warehouseCode);

    /**
     * 창고 검색
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND (w.warehouseName LIKE %:searchTerm% OR w.warehouseCode LIKE %:searchTerm% " +
           "OR w.description LIKE %:searchTerm% OR w.address LIKE %:searchTerm%)")
    Page<Warehouse> searchWarehouses(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 창고 검색
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND (:warehouseType IS NULL OR w.warehouseType = :warehouseType) " +
           "AND (:warehouseStatus IS NULL OR w.warehouseStatus = :warehouseStatus) " +
           "AND (:isActive IS NULL OR w.isActive = :isActive) " +
           "AND (:isOperational IS NULL OR (w.isActive = :isOperational AND w.warehouseStatus = 'ACTIVE')) " +
           "AND (:minCapacity IS NULL OR w.maxCapacity >= :minCapacity) " +
           "AND (:maxCapacity IS NULL OR w.maxCapacity <= :maxCapacity)")
    Page<Warehouse> searchWarehousesAdvanced(
            @Param("companyId") Long companyId,
            @Param("warehouseType") Warehouse.WarehouseType warehouseType,
            @Param("warehouseStatus") Warehouse.WarehouseStatus warehouseStatus,
            @Param("isActive") Boolean isActive,
            @Param("isOperational") Boolean isOperational,
            @Param("minCapacity") Double minCapacity,
            @Param("maxCapacity") Double maxCapacity,
            Pageable pageable
    );

    /**
     * 창고 유형별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    List<Warehouse> findByCompanyIdAndWarehouseTypeAndIsActiveTrueOrderByWarehouseNameAsc(
            Long companyId, Warehouse.WarehouseType warehouseType);

    /**
     * 운영 중인 창고 조회
     */
    @EntityGraph(attributePaths = {"company"})
    List<Warehouse> findByCompanyIdAndIsActiveTrueAndWarehouseStatusOrderByWarehouseNameAsc(
            Long companyId, Warehouse.WarehouseStatus warehouseStatus);

    /**
     * 용량 초과 창고 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.isActive = true AND w.maxCapacity IS NOT NULL " +
           "AND w.currentUsage > w.maxCapacity")
    List<Warehouse> findOverCapacityWarehouses(@Param("companyId") Long companyId);

    /**
     * 용량 부족 임박 창고 조회 (사용률 80% 이상)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.isActive = true AND w.maxCapacity IS NOT NULL AND w.maxCapacity > 0 " +
           "AND (w.currentUsage / w.maxCapacity) >= 0.8")
    List<Warehouse> findNearCapacityWarehouses(@Param("companyId") Long companyId);

    /**
     * 지역별 창고 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.address LIKE %:region% AND w.isActive = true " +
           "ORDER BY w.warehouseName ASC")
    List<Warehouse> findByRegion(@Param("companyId") Long companyId, @Param("region") String region);

    /**
     * GPS 좌표 범위 내 창고 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.latitude IS NOT NULL AND w.longitude IS NOT NULL " +
           "AND w.latitude BETWEEN :minLat AND :maxLat " +
           "AND w.longitude BETWEEN :minLng AND :maxLng " +
           "AND w.isActive = true")
    List<Warehouse> findByCoordinateRange(@Param("companyId") Long companyId,
                                        @Param("minLat") Double minLat,
                                        @Param("maxLat") Double maxLat,
                                        @Param("minLng") Double minLng,
                                        @Param("maxLng") Double maxLng);

    /**
     * 창고별 통계 조회
     */
    @Query("SELECT new com.erp.inventory.dto.WarehouseDto$WarehouseStatsDto(" +
           "w.id, w.warehouseName, " +
           "COUNT(i), " +
           "COALESCE(SUM(i.quantity), 0), " +
           "COUNT(CASE WHEN i.product.isActive = true THEN 1 END), " +
           "COUNT(CASE WHEN i.product.isActive = false THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity <= 0 THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity = 0 THEN 1 END), " +
           "COUNT(CASE WHEN i.quantity > i.maxStock THEN 1 END), " +
           "0, " +
           "0, " +
           "CASE WHEN w.maxCapacity > 0 THEN (w.currentUsage / w.maxCapacity * 100) ELSE 0 END, " +
           "0.0, " +
           ":now" +
           ") " +
           "FROM Warehouse w LEFT JOIN w.inventories i " +
           "WHERE w.company.id = :companyId " +
           "GROUP BY w.id, w.warehouseName, w.maxCapacity, w.currentUsage")
    List<com.erp.inventory.dto.WarehouseDto.WarehouseStatsDto> getWarehouseStats(
            @Param("companyId") Long companyId,
            @Param("now") java.time.LocalDateTime now);

    /**
     * 창고 용량 분석
     */
    @Query("SELECT new com.erp.inventory.dto.WarehouseDto$WarehouseCapacityDto(" +
           "w.id, w.warehouseName, " +
           "w.totalArea, w.usableArea, w.maxCapacity, w.currentUsage, " +
           "CASE WHEN w.maxCapacity > 0 THEN (w.currentUsage / w.maxCapacity * 100) ELSE 0 END, " +
           "CASE WHEN w.maxCapacity > 0 THEN (w.maxCapacity - w.currentUsage) ELSE 0 END, " +
           "CASE WHEN w.currentUsage > w.maxCapacity THEN true ELSE false END, " +
           "CASE " +
           "WHEN w.currentUsage > w.maxCapacity THEN '용량 초과' " +
           "WHEN w.maxCapacity > 0 AND (w.currentUsage / w.maxCapacity) >= 0.9 THEN '거의 가득참' " +
           "WHEN w.maxCapacity > 0 AND (w.currentUsage / w.maxCapacity) >= 0.8 THEN '용량 부족 임박' " +
           "WHEN w.maxCapacity > 0 AND (w.currentUsage / w.maxCapacity) >= 0.5 THEN '적정 수준' " +
           "ELSE '여유 있음' " +
           "END, " +
           "null" +
           ") " +
           "FROM Warehouse w WHERE w.company.id = :companyId AND w.isActive = true")
    List<com.erp.inventory.dto.WarehouseDto.WarehouseCapacityDto> getWarehouseCapacityAnalysis(@Param("companyId") Long companyId);

    /**
     * 창고 성과 분석
     */
    @Query("SELECT new com.erp.inventory.dto.WarehouseDto$WarehousePerformanceDto(" +
           "w.id, w.warehouseName, :startDate, :endDate, " +
           "COUNT(sm), " +
           "COUNT(CASE WHEN sm.movementType IN ('RECEIPT', 'PURCHASE_RECEIPT', 'PRODUCTION_RECEIPT', 'RETURN_RECEIPT', 'TRANSFER_IN', 'ADJUSTMENT_IN') THEN 1 END), " +
           "COUNT(CASE WHEN sm.movementType IN ('ISSUE', 'SALES_ISSUE', 'PRODUCTION_ISSUE', 'RETURN_ISSUE', 'TRANSFER_OUT', 'ADJUSTMENT_OUT', 'DISPOSAL') THEN 1 END), " +
           "0.0, 0.0, 0L, 0.0, null" +
           ") " +
           "FROM Warehouse w LEFT JOIN w.stockMovements sm " +
           "WHERE w.company.id = :companyId " +
           "AND (sm.movementDate IS NULL OR sm.movementDate BETWEEN :startDate AND :endDate) " +
           "AND (sm.movementStatus IS NULL OR sm.movementStatus = 'PROCESSED') " +
           "GROUP BY w.id, w.warehouseName")
    List<com.erp.inventory.dto.WarehouseDto.WarehousePerformanceDto> getWarehousePerformanceAnalysis(
            @Param("companyId") Long companyId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 창고 위치 정보 조회 (지도용)
     */
    @Query("SELECT new com.erp.inventory.dto.WarehouseDto$WarehouseLocationDto(" +
           "w.id, w.warehouseCode, w.warehouseName, w.warehouseType, " +
           "w.address, w.latitude, w.longitude, w.isActive, " +
           "CASE WHEN w.isActive = true AND w.warehouseStatus = 'ACTIVE' THEN true ELSE false END, " +
           "COUNT(i), " +
           "CASE WHEN w.maxCapacity > 0 THEN (w.currentUsage / w.maxCapacity * 100) ELSE 0 END, " +
           "CASE WHEN w.isActive = true AND w.warehouseStatus = 'ACTIVE' THEN 'green' ELSE 'red' END" +
           ") " +
           "FROM Warehouse w LEFT JOIN w.inventories i " +
           "WHERE w.company.id = :companyId " +
           "AND w.latitude IS NOT NULL AND w.longitude IS NOT NULL " +
           "GROUP BY w.id, w.warehouseCode, w.warehouseName, w.warehouseType, " +
           "w.address, w.latitude, w.longitude, w.isActive, w.warehouseStatus, w.maxCapacity, w.currentUsage")
    List<com.erp.inventory.dto.WarehouseDto.WarehouseLocationDto> getWarehouseLocations(@Param("companyId") Long companyId);

    /**
     * 창고 사용량 업데이트
     */
    @Modifying
    @Query("UPDATE Warehouse w SET w.currentUsage = " +
           "(SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.warehouse.id = w.id) " +
           "WHERE w.id = :warehouseId")
    void updateCurrentUsage(@Param("warehouseId") Long warehouseId);

    /**
     * 회사의 모든 창고 사용량 업데이트
     */
    @Modifying
    @Query("UPDATE Warehouse w SET w.currentUsage = " +
           "(SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.warehouse.id = w.id) " +
           "WHERE w.company.id = :companyId")
    void updateAllCurrentUsage(@Param("companyId") Long companyId);

    /**
     * 창고 상태 업데이트
     */
    @Modifying
    @Query("UPDATE Warehouse w SET w.warehouseStatus = :status WHERE w.id = :warehouseId")
    void updateWarehouseStatus(@Param("warehouseId") Long warehouseId, @Param("status") Warehouse.WarehouseStatus status);

    /**
     * 창고 환경 조건 확인
     */
    @Query("SELECT w FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.isActive = true " +
           "AND (:temperature IS NULL OR " +
           "     (w.temperatureMin IS NULL OR :temperature >= w.temperatureMin) AND " +
           "     (w.temperatureMax IS NULL OR :temperature <= w.temperatureMax)) " +
           "AND (:humidity IS NULL OR " +
           "     (w.humidityMin IS NULL OR :humidity >= w.humidityMin) AND " +
           "     (w.humidityMax IS NULL OR :humidity <= w.humidityMax))")
    List<Warehouse> findSuitableWarehouses(@Param("companyId") Long companyId,
                                         @Param("temperature") Double temperature,
                                         @Param("humidity") Double humidity);

    /**
     * 창고별 재고 품목 수 조회
     */
    @Query("SELECT w.id, COUNT(DISTINCT i.product.id) FROM Warehouse w LEFT JOIN w.inventories i " +
           "WHERE w.company.id = :companyId AND (i.quantity IS NULL OR i.quantity > 0) " +
           "GROUP BY w.id")
    List<Object[]> countProductsByWarehouse(@Param("companyId") Long companyId);

    /**
     * 창고별 재고 가치 조회
     */
    @Query("SELECT w.id, 0.0 FROM Warehouse w LEFT JOIN w.inventories i " +
           "WHERE w.company.id = :companyId " +
           "GROUP BY w.id")
    List<Object[]> getTotalStockValueByWarehouse(@Param("companyId") Long companyId);

    /**
     * 중복 확인 메서드들
     */
    boolean existsByCompanyIdAndWarehouseCodeAndIdNot(Long companyId, String warehouseCode, Long id);

    /**
     * 회사별 창고 수 조회
     */
    Long countByCompanyIdAndIsActiveTrue(Long companyId);

    /**
     * 운영 중인 창고 수 조회
     */
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.company.id = :companyId " +
           "AND w.isActive = true AND w.warehouseStatus = 'ACTIVE'")
    Long countOperationalWarehouses(@Param("companyId") Long companyId);

    /**
     * 최대 정렬 순서 조회
     */
    @Query("SELECT COALESCE(MAX(w.sortOrder), 0) FROM Warehouse w WHERE w.company.id = :companyId")
    Integer findMaxSortOrderByCompany(@Param("companyId") Long companyId);

    /**
     * 총 창고 용량 조회
     */
    @Query("SELECT COALESCE(SUM(w.maxCapacity), 0.0) FROM Warehouse w " +
           "WHERE w.company.id = :companyId AND w.isActive = true AND w.maxCapacity IS NOT NULL")
    Double getTotalCapacityByCompany(@Param("companyId") Long companyId);

    /**
     * 총 창고 사용량 조회
     */
    @Query("SELECT COALESCE(SUM(w.currentUsage), 0.0) FROM Warehouse w " +
           "WHERE w.company.id = :companyId AND w.isActive = true")
    Double getTotalUsageByCompany(@Param("companyId") Long companyId);

    /**
     * 창고 유형별 수 조회
     */
    @Query("SELECT w.warehouseType, COUNT(w) FROM Warehouse w " +
           "WHERE w.company.id = :companyId AND w.isActive = true " +
           "GROUP BY w.warehouseType")
    List<Object[]> countByWarehouseType(@Param("companyId") Long companyId);

    /**
     * 창고 상태별 수 조회
     */
    @Query("SELECT w.warehouseStatus, COUNT(w) FROM Warehouse w " +
           "WHERE w.company.id = :companyId " +
           "GROUP BY w.warehouseStatus")
    List<Object[]> countByWarehouseStatus(@Param("companyId") Long companyId);
}




