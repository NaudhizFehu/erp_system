package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 제품 카테고리 엔티티
 * 제품 분류를 관리합니다
 */
@Entity
@Table(name = "product_categories")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"company", "parentCategory", "subCategories", "products"})
@ToString(exclude = {"company", "parentCategory", "subCategories", "products"})
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory extends BaseEntity {

    @NotBlank(message = "카테고리코드는 필수입니다")
    @Size(max = 20, message = "카테고리코드는 20자 이하여야 합니다")
    @Column(name = "category_code", unique = true, nullable = false)
    private String categoryCode;

    @NotBlank(message = "카테고리명은 필수입니다")
    @Size(max = 100, message = "카테고리명은 100자 이하여야 합니다")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    /**
     * 회사 정보
     */
    @NotNull(message = "회사 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category_company"))
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private ProductCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private List<ProductCategory> subCategories;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}

