package com.erp.inventory.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.inventory.dto.ProductDto;
import com.erp.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 상품 관리 컨트롤러
 * 상품 등록, 수정, 조회 등의 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ProductDto.ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductDto.ProductCreateDto createDto) {
        try {
            log.info("상품 생성 요청 - 상품 코드: {}, 상품명: {}", createDto.productCode(), createDto.productName());
            
            ProductDto.ProductResponseDto result = productService.createProduct(createDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품이 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 생성 실패 - 상품 코드: {}", createDto.productCode(), e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ProductDto.ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto.ProductUpdateDto updateDto) {
        try {
            log.info("상품 수정 요청 - ID: {}", id);
            
            ProductDto.ProductResponseDto result = productService.updateProduct(id, updateDto);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품이 성공적으로 수정되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 수정 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 수정에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        try {
            log.info("상품 삭제 요청 - ID: {}", id);
            
            productService.deleteProduct(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품이 성공적으로 삭제되었습니다"
            ));
        } catch (Exception e) {
            log.error("상품 삭제 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 삭제에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 조회 (ID)
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER') or hasRole('SUPER_ADMIN')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<ProductDto.ProductResponseDto>> getProductById(@PathVariable Long id) {
        try {
            log.info("상품 조회 요청 - ID: {}", id);
            
            ProductDto.ProductResponseDto result = productService.getProductById(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 정보를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 조회 실패 - ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 상품 목록 조회
     */
    @GetMapping("/companies/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummaryDto>>> getProductsByCompany(
            @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 상품 목록 조회 요청 - 회사: {}", companyId);
            
            Page<ProductDto.ProductSummaryDto> result = productService.getProductsByCompany(companyId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 상품 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("회사별 상품 목록 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 상품 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 분류별 상품 목록 조회
     */
    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummaryDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("분류별 상품 목록 조회 요청 - 분류: {}", categoryId);
            
            Page<ProductDto.ProductSummaryDto> result = productService.getProductsByCategory(categoryId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "분류별 상품 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("분류별 상품 목록 조회 실패 - 분류: {}", categoryId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("분류별 상품 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 검색
     */
    @GetMapping("/companies/{companyId}/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummaryDto>>> searchProducts(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("상품 검색 요청 - 회사: {}, 검색어: {}", companyId, searchTerm);
            
            Page<ProductDto.ProductSummaryDto> result = productService.searchProducts(companyId, searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 검색 실패 - 회사: {}, 검색어: {}", companyId, searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 고급 상품 검색
     */
    @PostMapping("/companies/{companyId}/search/advanced")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<ProductDto.ProductSummaryDto>>> searchProductsAdvanced(
            @PathVariable Long companyId,
            @Valid @RequestBody ProductDto.ProductSearchDto searchDto,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고급 상품 검색 요청 - 회사: {}", companyId);
            
            Page<ProductDto.ProductSummaryDto> result = productService.searchProductsAdvanced(companyId, searchDto, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "고급 상품 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("고급 상품 검색 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("고급 상품 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 바코드로 상품 조회
     */
    @GetMapping("/barcode/{barcode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto.ProductResponseDto>> getProductByBarcode(@PathVariable String barcode) {
        try {
            log.info("바코드 상품 조회 요청 - 바코드: {}", barcode);
            
            ProductDto.ProductResponseDto result = productService.getProductByBarcode(barcode);
            
            return ResponseEntity.ok(ApiResponse.success(
                "바코드 상품 정보를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("바코드 상품 조회 실패 - 바코드: {}", barcode, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("바코드 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }


    /**
     * 상품명으로 검색 (테스트용)
     */
    @GetMapping("/search/name/{name}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER') or hasRole('SUPER_ADMIN')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<List<ProductDto.ProductResponseDto>>> searchProductsByName(@PathVariable String name) {
        try {
            log.info("상품명 검색 요청 - 이름: {}", name);
            
            List<ProductDto.ProductResponseDto> result = productService.searchProductsByName(name);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품명 검색을 성공적으로 완료했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품명 검색 실패 - 이름: {}", name, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품명 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }


    /**
     * 재주문 필요 상품 조회
     */
    @GetMapping("/companies/{companyId}/reorder-needed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ProductDto.ProductSummaryDto>>> getReorderNeededProducts(@PathVariable Long companyId) {
        try {
            log.info("재주문 필요 상품 조회 요청 - 회사: {}", companyId);
            
            List<ProductDto.ProductSummaryDto> result = productService.getReorderNeededProducts(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재주문 필요 상품을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재주문 필요 상품 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재주문 필요 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }


    /**
     * 브랜드별 상품 수 조회
     */
    @GetMapping("/companies/{companyId}/brands/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<Object[]>>> getBrandStatistics(@PathVariable Long companyId) {
        try {
            log.info("브랜드별 상품 수 조회 요청 - 회사: {}", companyId);
            
            List<Object[]> result = productService.getBrandStatistics(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "브랜드별 상품 통계를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("브랜드별 상품 수 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("브랜드별 상품 통계 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 재고 현황 조회
     */
    @GetMapping("/{id}/stock-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<ProductDto.ProductStockDto>> getProductStockStatus(@PathVariable Long id) {
        try {
            log.info("상품 재고 현황 조회 요청 - 상품 ID: {}", id);
            
            ProductDto.ProductStockDto result = productService.getProductStockStatus(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 재고 현황을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 재고 현황 조회 실패 - 상품 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 재고 현황 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 활성화/비활성화
     */
    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ProductDto.ProductResponseDto>> toggleProductActive(@PathVariable Long id) {
        try {
            log.info("상품 활성화/비활성화 요청 - 상품 ID: {}", id);
            
            ProductDto.ProductResponseDto result = productService.toggleProductActive(id);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 활성화 상태가 성공적으로 변경되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("상품 활성화/비활성화 실패 - 상품 ID: {}", id, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 활성화 상태 변경에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 재고 상태 업데이트
     */
    @PostMapping("/companies/{companyId}/update-stock-statuses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateProductStockStatuses(@PathVariable Long companyId) {
        try {
            log.info("상품 재고 상태 업데이트 요청 - 회사: {}", companyId);
            
            productService.updateProductStockStatuses(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 재고 상태가 성공적으로 업데이트되었습니다"
            ));
        } catch (Exception e) {
            log.error("상품 재고 상태 업데이트 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 재고 상태 업데이트에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 부족 상품 조회
     */
    @GetMapping("/companies/{companyId}/low-stock")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<List<ProductDto.ProductSummaryDto>>> getLowStockProducts(@PathVariable Long companyId) {
        try {
            log.info("재고 부족 상품 조회 요청 - 회사: {}", companyId);
            
            List<ProductDto.ProductSummaryDto> result = productService.getLowStockProducts(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 부족 상품 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 부족 상품 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 부족 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재고 없음 상품 조회
     */
    @GetMapping("/companies/{companyId}/out-of-stock")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<List<ProductDto.ProductSummaryDto>>> getOutOfStockProducts(@PathVariable Long companyId) {
        try {
            log.info("재고 없음 상품 조회 요청 - 회사: {}", companyId);
            
            List<ProductDto.ProductSummaryDto> result = productService.getOutOfStockProducts(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재고 없음 상품 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재고 없음 상품 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재고 없음 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 통계 조회
     */
    @GetMapping("/companies/{companyId}/statistics")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<Object>> getProductStatistics(@PathVariable Long companyId) {
        try {
            log.info("상품 통계 조회 요청 - 회사: {}", companyId);
            
            // 임시 통계 데이터 반환
            var stats = new java.util.HashMap<String, Object>();
            stats.put("totalProducts", 4);
            stats.put("activeProducts", 4);
            stats.put("lowStockProducts", 0);
            stats.put("outOfStockProducts", 0);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 통계를 성공적으로 조회했습니다",
                stats
            ));
        } catch (Exception e) {
            log.error("상품 통계 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 통계 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 카테고리 목록 조회
     */
    @GetMapping("/categories/companies/{companyId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")  // 개발/테스트용으로 임시 비활성화
    public ResponseEntity<ApiResponse<List<Object>>> getCategoriesByCompany(@PathVariable Long companyId) {
        try {
            log.info("회사별 카테고리 목록 조회 요청 - 회사: {}", companyId);
            
            // 임시 카테고리 데이터 반환
            var categories = new java.util.ArrayList<Object>();
            var category1 = new java.util.HashMap<String, Object>();
            category1.put("id", 1);
            category1.put("name", "전자제품");
            category1.put("fullPath", "전자제품");
            categories.add(category1);
            
            var category2 = new java.util.HashMap<String, Object>();
            category2.put("id", 2);
            category2.put("name", "의류");
            category2.put("fullPath", "의류");
            categories.add(category2);
            
            var category3 = new java.util.HashMap<String, Object>();
            category3.put("id", 3);
            category3.put("name", "사무용품");
            category3.put("fullPath", "사무용품");
            categories.add(category3);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 카테고리 목록을 성공적으로 조회했습니다",
                categories
            ));
        } catch (Exception e) {
            log.error("회사별 카테고리 목록 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 카테고리 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 상품 코드 중복 확인
     */
    @GetMapping("/companies/{companyId}/check-code/{productCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> checkProductCodeDuplicate(
            @PathVariable Long companyId,
            @PathVariable String productCode,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("상품 코드 중복 확인 요청 - 회사: {}, 코드: {}", companyId, productCode);
            
            Boolean isDuplicate = productService.checkProductCodeDuplicate(companyId, productCode, excludeId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "상품 코드 중복 확인이 완료되었습니다",
                isDuplicate
            ));
        } catch (Exception e) {
            log.error("상품 코드 중복 확인 실패 - 회사: {}, 코드: {}", companyId, productCode, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("상품 코드 중복 확인에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 바코드 중복 확인
     */
    @GetMapping("/check-barcode/{barcode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> checkBarcodeDuplicate(
            @PathVariable String barcode,
            @RequestParam(required = false) Long excludeId) {
        try {
            log.info("바코드 중복 확인 요청 - 바코드: {}", barcode);
            
            Boolean isDuplicate = productService.checkBarcodeDuplicate(barcode, excludeId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "바코드 중복 확인이 완료되었습니다",
                isDuplicate
            ));
        } catch (Exception e) {
            log.error("바코드 중복 확인 실패 - 바코드: {}", barcode, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("바코드 중복 확인에 실패했습니다: " + e.getMessage())
            );
        }
    }
}
