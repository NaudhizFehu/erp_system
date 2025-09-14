package com.erp.inventory.repository;

import com.erp.inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 회사별 상품 조회 (활성화된 것만)
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Page<Product> findByCompanyIdAndIsActiveTrueOrderByProductNameAsc(Long companyId, Pageable pageable);

    /**
     * 회사별 모든 상품 조회
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Page<Product> findByCompanyIdOrderByProductNameAsc(Long companyId, Pageable pageable);

    /**
     * 회사 및 상품 코드로 조회
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Optional<Product> findByCompanyIdAndProductCode(Long companyId, String productCode);

    /**
     * 바코드로 조회
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Optional<Product> findByBarcode(String barcode);

    /**
     * SKU로 조회
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Optional<Product> findByCompanyIdAndSku(Long companyId, String sku);

    /**
     * 분류별 상품 조회
     */
    @EntityGraph(attributePaths = {"company", "category"})
    Page<Product> findByCategoryIdAndIsActiveTrueOrderByProductNameAsc(Long categoryId, Pageable pageable);

    /**
     * 분류별 모든 상품 조회 (하위 분류 포함)
     */
    @EntityGraph(attributePaths = {"company", "category"})
    @Query("SELECT p FROM Product p WHERE p.category.id IN " +
           "(SELECT c.id FROM ProductCategory c WHERE c.id = :categoryId OR c.parentCategory.id = :categoryId) " +
           "AND p.isActive = true ORDER BY p.productName ASC")
    Page<Product> findByCategoryIncludingSubCategories(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * 상품 검색 (통합 검색)
     */
    @EntityGraph(attributePaths = {"company", "category"})
    @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
           "AND (p.productName LIKE %:searchTerm% OR p.productCode LIKE %:searchTerm% " +
           "OR p.description LIKE %:searchTerm% OR p.barcode LIKE %:searchTerm% " +
           "OR p.sku LIKE %:searchTerm% OR p.brand LIKE %:searchTerm% " +
           "OR p.manufacturer LIKE %:searchTerm%)")
    Page<Product> searchProducts(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    @EntityGraph(attributePaths = {"company", "category"})
    @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:productType IS NULL OR p.productType = :productType) " +
           "AND (:productStatus IS NULL OR p.productStatus = :productStatus) " +
           "AND (:isActive IS NULL OR p.isActive = :isActive) " +
           "AND (:trackInventory IS NULL OR p.trackInventory = :trackInventory) " +
           "AND (:brand IS NULL OR p.brand LIKE %:brand%) " +
           "AND (:manufacturer IS NULL OR p.manufacturer LIKE %:manufacturer%) " +
           "AND (:supplier IS NULL OR p.supplier LIKE %:supplier%) " +
           "AND (:minPrice IS NULL OR p.sellingPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.sellingPrice <= :maxPrice)")
    Page<Product> searchProductsAdvanced(
            @Param("companyId") Long companyId,
            @Param("categoryId") Long categoryId,
            @Param("productType") Product.ProductType productType,
            @Param("productStatus") Product.ProductStatus productStatus,
            @Param("isActive") Boolean isActive,
            @Param("trackInventory") Boolean trackInventory,
            @Param("brand") String brand,
            @Param("manufacturer") String manufacturer,
            @Param("supplier") String supplier,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    /**
     * 안전재고 미달 상품 조회
     * isLowStock은 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @EntityGraph(attributePaths = {"company", "category"})
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "AND p.isLowStock = true")
    // List<Product> findLowStockProducts(@Param("companyId") Long companyId);

    /**
     * 재고없음 상품 조회
     * isOutOfStock은 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @EntityGraph(attributePaths = {"company", "category"})
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "AND p.isOutOfStock = true")
    // List<Product> findOutOfStockProducts(@Param("companyId") Long companyId);

    /**
     * 과재고 상품 조회
     * isOverStock은 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @EntityGraph(attributePaths = {"company", "category"})
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "AND p.isOverStock = true")
    // List<Product> findOverStockProducts(@Param("companyId") Long companyId);

    /**
     * 재주문 필요 상품 조회
     * needsReorder는 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @EntityGraph(attributePaths = {"company", "category"})
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "AND p.needsReorder = true")
    // List<Product> findReorderNeededProducts(@Param("companyId") Long companyId);

    /**
     * 판매 가능한 상품 조회
     * isSellable은 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @EntityGraph(attributePaths = {"company", "category"})
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.productStatus = 'ACTIVE' " +
    //        "AND p.isSellable = true")
    // List<Product> findSellableProducts(@Param("companyId") Long companyId);

    /**
     * 브랜드별 상품 수 조회
     */
    @Query("SELECT p.brand, COUNT(p) FROM Product p WHERE p.company.id = :companyId " +
           "AND p.brand IS NOT NULL AND p.isActive = true " +
           "GROUP BY p.brand ORDER BY COUNT(p) DESC")
    List<Object[]> countProductsByBrand(@Param("companyId") Long companyId);

    /**
     * 제조사별 상품 수 조회
     */
    @Query("SELECT p.manufacturer, COUNT(p) FROM Product p WHERE p.company.id = :companyId " +
           "AND p.manufacturer IS NOT NULL AND p.isActive = true " +
           "GROUP BY p.manufacturer ORDER BY COUNT(p) DESC")
    List<Object[]> countProductsByManufacturer(@Param("companyId") Long companyId);

    /**
     * 상품 유형별 통계
     */
    // @Query("SELECT p.productType, COUNT(p), AVG(p.sellingPrice), SUM(p.totalStock) " +
    //        "FROM Product p WHERE p.company.id = :companyId AND p.isActive = true " +
    //        "GROUP BY p.productType")
    // List<Object[]> getProductTypeStats(@Param("companyId") Long companyId);

    /**
     * 상품 통계 조회
     * isLowStock, isOutOfStock, isOverStock, needsReorder는 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT new com.erp.inventory.dto.ProductDto$ProductStatsDto(" +
    //        "COUNT(p), " +
    //        "COUNT(CASE WHEN p.isActive = true THEN 1 END), " +
    //        "COUNT(CASE WHEN p.isActive = false THEN 1 END), " +
    //        "COUNT(CASE WHEN p.isActive = true AND p.isLowStock = true THEN 1 END), " +
    //        "COUNT(CASE WHEN p.isActive = true AND p.isOutOfStock = true THEN 1 END), " +
    //        "COUNT(CASE WHEN p.isActive = true AND p.isOverStock = true THEN 1 END), " +
    //        "COUNT(CASE WHEN p.isActive = true AND p.needsReorder = true THEN 1 END), " +
    //        "COALESCE(SUM(CASE WHEN p.isActive = true THEN " +
    //        "  (SELECT COALESCE(SUM(i.totalStockValue), 0) FROM Inventory i WHERE i.product = p) " +
    //        "END), 0), " +
    //        "COALESCE(AVG(CASE WHEN p.isActive = true THEN " +
    //        "  (SELECT COALESCE(SUM(i.totalStockValue), 0) FROM Inventory i WHERE i.product = p) " +
    //        "END), 0)" +
    //        ") " +
    //        "FROM Product p WHERE p.company.id = :companyId")
    // com.erp.inventory.dto.ProductDto.ProductStatsDto getProductStats(@Param("companyId") Long companyId);

    /**
     * 평균 원가 업데이트
     */
    @Modifying
    @Query("UPDATE Product p SET p.averageCost = :averageCost WHERE p.id = :productId")
    void updateAverageCost(@Param("productId") Long productId, @Param("averageCost") BigDecimal averageCost);

    /**
     * 최근 매입 단가 업데이트
     */
    @Modifying
    @Query("UPDATE Product p SET p.lastPurchasePrice = :lastPurchasePrice WHERE p.id = :productId")
    void updateLastPurchasePrice(@Param("productId") Long productId, @Param("lastPurchasePrice") BigDecimal lastPurchasePrice);

    /**
     * 재고 상태 플래그 업데이트
     * isLowStock, isOutOfStock, isOverStock, needsReorder는 메서드이므로 필드로 업데이트할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Modifying
    // @Query("UPDATE Product p SET p.isLowStock = :isLowStock, p.isOutOfStock = :isOutOfStock, " +
    //        "p.isOverStock = :isOverStock, p.needsReorder = :needsReorder WHERE p.id = :productId")
    // void updateStockFlags(@Param("productId") Long productId, 
    //                      @Param("isLowStock") Boolean isLowStock,
    //                      @Param("isOutOfStock") Boolean isOutOfStock,
    //                      @Param("isOverStock") Boolean isOverStock,
    //                      @Param("needsReorder") Boolean needsReorder);

    /**
     * 상품 총 재고 수량 업데이트
     * totalStock은 계산된 값이므로 별도 업데이트가 불필요
     */
    // @Modifying
    // @Query("UPDATE Product p SET p.totalStock = " +
    //        "(SELECT COALESCE(SUM(i.currentStock), 0) FROM Inventory i WHERE i.product.id = p.id) " +
    //        "WHERE p.id = :productId")
    // void updateTotalStock(@Param("productId") Long productId);

    /**
     * 회사의 모든 상품 재고 수량 업데이트
     * totalStock은 계산된 값이므로 별도 업데이트가 불필요
     */
    // @Modifying
    // @Query("UPDATE Product p SET p.totalStock = " +
    //        "(SELECT COALESCE(SUM(i.currentStock), 0) FROM Inventory i WHERE i.product.id = p.id) " +
    //        "WHERE p.company.id = :companyId")
    // void updateAllTotalStock(@Param("companyId") Long companyId);

    /**
     * 가격대별 상품 분포 조회
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN p.sellingPrice < 10000 THEN '1만원 미만' " +
           "WHEN p.sellingPrice < 50000 THEN '1-5만원' " +
           "WHEN p.sellingPrice < 100000 THEN '5-10만원' " +
           "WHEN p.sellingPrice < 500000 THEN '10-50만원' " +
           "ELSE '50만원 이상' " +
           "END as priceRange, " +
           "COUNT(p) as count " +
           "FROM Product p WHERE p.company.id = :companyId AND p.isActive = true " +
           "GROUP BY " +
           "CASE " +
           "WHEN p.sellingPrice < 10000 THEN '1만원 미만' " +
           "WHEN p.sellingPrice < 50000 THEN '1-5만원' " +
           "WHEN p.sellingPrice < 100000 THEN '5-10만원' " +
           "WHEN p.sellingPrice < 500000 THEN '10-50만원' " +
           "ELSE '50만원 이상' " +
           "END " +
           "ORDER BY MIN(p.sellingPrice)")
    List<Object[]> getPriceRangeDistribution(@Param("companyId") Long companyId);

    /**
     * 재고 회전율 상위 상품 조회
     * stockTurnoverRate는 메서드이므로 JPQL에서 직접 사용할 수 없음
     * 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "ORDER BY p.stockTurnoverRate DESC")
    // List<Product> findTopStockTurnoverProducts(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 수익성 상위 상품 조회
     */
    @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
           "AND p.isActive = true AND p.sellingPrice > 0 AND p.averageCost > 0 " +
           "ORDER BY ((p.sellingPrice - p.averageCost) / p.sellingPrice) DESC")
    List<Product> findTopProfitableProducts(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 중복 확인 메서드들
     */
    boolean existsByCompanyIdAndProductCodeAndIdNot(Long companyId, String productCode, Long id);
    boolean existsByBarcodeAndIdNot(String barcode, Long id);
    boolean existsByCompanyIdAndSkuAndIdNot(Long companyId, String sku, Long id);

    /**
     * 회사별 상품 수 조회
     */
    Long countByCompanyIdAndIsActiveTrue(Long companyId);

    /**
     * 분류별 상품 수 조회
     */
    Long countByCategoryIdAndIsActiveTrue(Long categoryId);

    /**
     * 최대 정렬 순서 조회
     */
    @Query("SELECT COALESCE(MAX(p.sortOrder), 0) FROM Product p WHERE p.company.id = :companyId")
    Integer findMaxSortOrderByCompany(@Param("companyId") Long companyId);

    /**
     * ABC 분석용 데이터 조회
     */
    // @Query("SELECT new com.erp.inventory.dto.InventoryDto$ABCAnalysisDto(" +
    //        "p.id, p.productCode, p.productName, " +
    //        "(p.averageCost * p.totalStock), " +
    //        "0.0, '', ''" +
    //        ") " +
    //        "FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "ORDER BY (p.averageCost * p.totalStock) DESC")
    // List<com.erp.inventory.dto.InventoryDto.ABCAnalysisDto> getAbcAnalysisData(@Param("companyId") Long companyId);

    /**
     * 재고 가치 상위 상품 조회
     */
    // @Query("SELECT p FROM Product p WHERE p.company.id = :companyId " +
    //        "AND p.isActive = true AND p.trackInventory = true " +
    //        "ORDER BY (p.averageCost * p.totalStock) DESC")
    // List<Product> findTopStockValueProducts(@Param("companyId") Long companyId, Pageable pageable);

    // ==================== Dashboard용 메서드들 ====================
    
    /**
     * 회사별 삭제되지 않은 상품 수
     */
    long countByCompanyIdAndIsDeletedFalse(Long companyId);

    /**
     * 전역 검색용 - 회사별 상품명으로 검색
     */
    @EntityGraph(attributePaths = {"company", "category"})
    List<Product> findByCompanyIdAndProductNameContainingIgnoreCase(Long companyId, String productName);
}
