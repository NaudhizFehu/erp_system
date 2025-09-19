package com.erp.inventory.service;

import com.erp.inventory.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 서비스 인터페이스
 * 상품 관련 비즈니스 로직을 정의합니다
 */
public interface ProductService {

    /**
     * 상품 생성
     */
    ProductDto.ProductResponseDto createProduct(ProductDto.ProductCreateDto createDto);

    /**
     * 상품 수정
     */
    ProductDto.ProductResponseDto updateProduct(Long id, ProductDto.ProductUpdateDto updateDto);

    /**
     * 상품 조회 (ID)
     */
    ProductDto.ProductResponseDto getProduct(Long id);

    /**
     * 상품 코드로 조회
     */
    ProductDto.ProductResponseDto getProductByCode(String productCode);

    /**
     * 바코드로 상품 조회
     */
    ProductDto.ProductResponseDto getProductByBarcode(String barcode);

    /**
     * 전체 상품 목록 조회 (페이징)
     */
    Page<ProductDto.ProductResponseDto> getAllProducts(Pageable pageable);

    /**
     * 회사별 상품 목록 조회
     */
    Page<ProductDto.ProductSummaryDto> getProductsByCompany(Long companyId, Pageable pageable);

    /**
     * 카테고리별 상품 목록 조회
     */
    Page<ProductDto.ProductSummaryDto> getProductsByCategory(Long categoryId, Pageable pageable);

    /**
     * 활성 상품 목록 조회
     */
    List<ProductDto.ProductResponseDto> getActiveProducts();

    /**
     * 상품 검색
     */
    Page<ProductDto.ProductResponseDto> searchProducts(String searchTerm, Pageable pageable);

    /**
     * 상품명으로 검색
     */
    List<ProductDto.ProductResponseDto> searchProductsByName(String name);

    /**
     * 가격 범위로 상품 조회
     */
    Page<ProductDto.ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);


    /**
     * 상품 삭제 (소프트 삭제)
     */
    void deleteProduct(Long id);

    /**
     * 상품 코드 중복 확인
     */
    boolean isProductCodeExists(String productCode);

    /**
     * 바코드 중복 확인
     */
    boolean isBarcodeExists(String barcode);

    /**
     * 카테고리별 상품 수 통계
     */
    List<Object[]> getProductCountByCategory();

    /**
     * 상품 상태별 통계
     */
    List<Object[]> getProductCountByStatus();

    /**
     * 재고 상태별 상품 수 통계
     */
    List<Object[]> getProductCountByStockStatus();

    // ==================== ProductController에서 사용하는 추가 메서드들 ====================
    
    /**
     * ID로 상품 조회
     */
    ProductDto.ProductResponseDto getProductById(Long id);
    
    /**
     * 회사별 상품 검색
     */
    Page<ProductDto.ProductSummaryDto> searchProducts(Long companyId, String searchTerm, Pageable pageable);
    
    /**
     * 고급 검색
     */
    Page<ProductDto.ProductSummaryDto> searchProductsAdvanced(Long companyId, ProductDto.ProductSearchDto searchDto, Pageable pageable);
    
    /**
     * 회사별 재고 부족 상품 조회
     */
    List<ProductDto.ProductSummaryDto> getLowStockProducts(Long companyId);
    
    /**
     * 회사별 재고 없음 상품 조회
     */
    List<ProductDto.ProductSummaryDto> getOutOfStockProducts(Long companyId);
    
    /**
     * 재주문 필요 상품 조회
     */
    List<ProductDto.ProductSummaryDto> getReorderNeededProducts(Long companyId);
    
    /**
     * 상품 통계
     */
    ProductDto.ProductStatsDto getProductStats(Long companyId);
    
    /**
     * 브랜드 통계
     */
    List<Object[]> getBrandStatistics(Long companyId);
    
    /**
     * 상품 재고 상태
     */
    ProductDto.ProductStockDto getProductStockStatus(Long productId);
    
    /**
     * 상품 활성화 토글
     */
    ProductDto.ProductResponseDto toggleProductActive(Long id);
    
    /**
     * 재고 상태 업데이트
     */
    void updateProductStockStatuses(Long companyId);
    
    /**
     * 상품 코드 중복 확인 (회사별, 수정 시)
     */
    boolean checkProductCodeDuplicate(Long companyId, String productCode, Long excludeId);
    
    /**
     * 바코드 중복 확인 (수정 시)
     */
    boolean checkBarcodeDuplicate(String barcode, Long excludeId);
}
