package com.erp.inventory.repository;

import com.erp.inventory.entity.ProductCategory;
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
 * 상품분류 Repository
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    /**
     * 회사별 분류 조회 (활성화된 것만)
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    List<ProductCategory> findByIsActiveTrueOrderByNameAsc();

    /**
     * 회사별 모든 분류 조회
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    List<ProductCategory> findAllByOrderByNameAsc();

    /**
     * 루트 분류 조회
     */
    @EntityGraph(attributePaths = {})
    List<ProductCategory> findByParentCategoryIsNullOrderByNameAsc();

    /**
     * 상위 분류별 하위 분류 조회
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    List<ProductCategory> findByParentCategoryIdOrderByNameAsc(Long parentCategoryId);

    /**
     * 분류 코드로 조회
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    Optional<ProductCategory> findByCategoryCode(String categoryCode);

    /**
     * 분류 검색
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    @Query("SELECT c FROM ProductCategory c WHERE " +
           "(c.name LIKE %:searchTerm% OR c.categoryCode LIKE %:searchTerm% OR c.description LIKE %:searchTerm%) " +
           "ORDER BY c.name ASC")
    List<ProductCategory> searchByTerm(@Param("searchTerm") String searchTerm);

    /**
     * 분류 검색 (페이징)
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    @Query("SELECT c FROM ProductCategory c WHERE " +
           "(c.name LIKE %:searchTerm% OR c.categoryCode LIKE %:searchTerm% OR c.description LIKE %:searchTerm%)")
    Page<ProductCategory> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 상품을 가진 분류 조회
     */
    @EntityGraph(attributePaths = {"parentCategory"})
    @Query("SELECT DISTINCT c FROM ProductCategory c JOIN c.products p WHERE p.isActive = true")
    List<ProductCategory> findCategoriesWithActiveProducts();

    /**
     * 분류별 상품 수 조회
     */
    @Query("SELECT c.id, COUNT(p.id) FROM ProductCategory c LEFT JOIN c.products p " +
           "WHERE (p.isActive = true OR p.id IS NULL) " +
           "GROUP BY c.id")
    List<Object[]> countProductsByCategory();

    /**
     * 분류 삭제 가능 여부 확인 (하위 분류나 상품이 있는지)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 OR COUNT(p) > 0 THEN false ELSE true END " +
           "FROM ProductCategory parent " +
           "LEFT JOIN ProductCategory c ON c.parentCategory.id = parent.id " +
           "LEFT JOIN Product p ON p.category.id = parent.id " +
           "WHERE parent.id = :categoryId")
    Boolean canDeleteCategory(@Param("categoryId") Long categoryId);

    /**
     * 분류명 중복 확인
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * 분류 코드 중복 확인
     */
    boolean existsByCategoryCodeAndIdNot(String categoryCode, Long id);

    /**
     * 활성 분류 수 조회
     */
    @Query("SELECT COUNT(c) FROM ProductCategory c WHERE c.isActive = true")
    Long countActiveCategories();
}




