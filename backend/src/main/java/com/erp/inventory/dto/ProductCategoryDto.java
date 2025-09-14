package com.erp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 상품 카테고리 DTO
 */
public class ProductCategoryDto {

    /**
     * 상품 카테고리 생성 DTO
     */
    public record ProductCategoryCreateDto(
            @NotBlank(message = "카테고리코드는 필수입니다")
            @Size(max = 20, message = "카테고리코드는 20자 이하여야 합니다")
            String categoryCode,
            
            @NotBlank(message = "카테고리명은 필수입니다")
            @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다")
            String name,
            
            String description,
            Long parentCategoryId
    ) {}

    /**
     * 상품 카테고리 수정 DTO
     */
    public record ProductCategoryUpdateDto(
            @Size(max = 20, message = "카테고리코드는 20자 이하여야 합니다")
            String categoryCode,
            
            @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다")
            String name,
            
            String description,
            Long parentCategoryId,
            Boolean isActive
    ) {}

    /**
     * 상품 카테고리 응답 DTO
     */
    public record ProductCategoryResponseDto(
            Long id,
            String categoryCode,
            String name,
            String description,
            ProductCategoryResponseDto parentCategory,
            List<ProductCategoryResponseDto> subCategories,
            Boolean isActive
    ) {}
}
