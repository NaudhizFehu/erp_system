package com.erp.inventory.service.impl;

import com.erp.inventory.dto.ProductCategoryDto;
import com.erp.inventory.repository.ProductCategoryRepository;
import com.erp.inventory.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

/**
 * 상품 카테고리 서비스 구현체 (기본 구현)
 * TODO: 실제 비즈니스 로직 구현 필요
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Transactional
    public ProductCategoryDto.ProductCategoryResponseDto createCategory(ProductCategoryDto.ProductCategoryCreateDto createDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public ProductCategoryDto.ProductCategoryResponseDto updateCategory(Long id, ProductCategoryDto.ProductCategoryUpdateDto updateDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public ProductCategoryDto.ProductCategoryResponseDto getCategory(Long id) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public ProductCategoryDto.ProductCategoryResponseDto getCategoryByCode(String categoryCode) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public Page<ProductCategoryDto.ProductCategoryResponseDto> getAllCategories(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public List<ProductCategoryDto.ProductCategoryResponseDto> getSubCategories(Long parentCategoryId) {
        return new ArrayList<>();
    }

    @Override
    public List<ProductCategoryDto.ProductCategoryResponseDto> getRootCategories() {
        return new ArrayList<>();
    }

    @Override
    public List<ProductCategoryDto.ProductCategoryResponseDto> getActiveCategories() {
        return new ArrayList<>();
    }

    @Override
    public Page<ProductCategoryDto.ProductCategoryResponseDto> searchCategories(String searchTerm, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public boolean isCategoryCodeExists(String categoryCode) {
        return false;
    }

    @Override
    public List<Object[]> getProductCountByCategory() {
        return new ArrayList<>();
    }
}




