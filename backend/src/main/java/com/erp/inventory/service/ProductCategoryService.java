package com.erp.inventory.service;

import com.erp.inventory.dto.ProductCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 상품 카테고리 서비스 인터페이스
 * 상품 카테고리 관련 비즈니스 로직을 정의합니다
 */
public interface ProductCategoryService {

    /**
     * 카테고리 생성
     */
    ProductCategoryDto.ProductCategoryResponseDto createCategory(ProductCategoryDto.ProductCategoryCreateDto createDto);

    /**
     * 카테고리 수정
     */
    ProductCategoryDto.ProductCategoryResponseDto updateCategory(Long id, ProductCategoryDto.ProductCategoryUpdateDto updateDto);

    /**
     * 카테고리 조회 (ID)
     */
    ProductCategoryDto.ProductCategoryResponseDto getCategory(Long id);

    /**
     * 카테고리 코드로 조회
     */
    ProductCategoryDto.ProductCategoryResponseDto getCategoryByCode(String categoryCode);

    /**
     * 전체 카테고리 목록 조회 (페이징)
     */
    Page<ProductCategoryDto.ProductCategoryResponseDto> getAllCategories(Pageable pageable);

    /**
     * 상위 카테고리별 하위 카테고리 조회
     */
    List<ProductCategoryDto.ProductCategoryResponseDto> getSubCategories(Long parentCategoryId);

    /**
     * 최상위 카테고리 목록 조회
     */
    List<ProductCategoryDto.ProductCategoryResponseDto> getRootCategories();

    /**
     * 활성 카테고리 목록 조회
     */
    List<ProductCategoryDto.ProductCategoryResponseDto> getActiveCategories();

    /**
     * 카테고리 검색
     */
    Page<ProductCategoryDto.ProductCategoryResponseDto> searchCategories(String searchTerm, Pageable pageable);

    /**
     * 카테고리 삭제 (소프트 삭제)
     */
    void deleteCategory(Long id);

    /**
     * 카테고리 코드 중복 확인
     */
    boolean isCategoryCodeExists(String categoryCode);

    /**
     * 카테고리별 상품 수 통계
     */
    List<Object[]> getProductCountByCategory();
}




