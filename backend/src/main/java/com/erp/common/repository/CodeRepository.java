package com.erp.common.repository;

import com.erp.common.entity.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 공통코드 레포지토리
 * 공통코드 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    /**
     * 그룹코드와 코드값으로 코드 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND c.codeValue = :codeValue AND c.isDeleted = false")
    Optional<Code> findByGroupCodeAndCodeValue(@Param("groupCode") String groupCode, @Param("codeValue") String codeValue);

    /**
     * 그룹코드별 코드 목록 조회 (활성화된 것만)
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND c.isActive = true AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findActiveByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 그룹코드별 전체 코드 목록 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 상위 코드별 하위 코드 목록 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.parentCode.id = :parentCodeId AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findByParentCodeId(@Param("parentCodeId") Long parentCodeId);

    /**
     * 상위 코드별 활성 하위 코드 목록 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.parentCode.id = :parentCodeId AND c.isActive = true AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findActiveByParentCodeId(@Param("parentCodeId") Long parentCodeId);

    /**
     * 최상위 코드 목록 조회 (상위 코드가 없는 코드들)
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.company comp WHERE c.parentCode IS NULL AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder, c.codeValue")
    List<Code> findRootCodes();

    /**
     * 그룹코드별 최상위 코드 목록 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND c.parentCode IS NULL AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findRootCodesByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 코드명으로 검색
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.codeName LIKE %:codeName% AND c.isDeleted = false")
    List<Code> findByCodeNameContaining(@Param("codeName") String codeName);

    /**
     * 그룹코드별 코드명 검색
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND c.codeName LIKE %:codeName% AND c.isDeleted = false")
    List<Code> findByGroupCodeAndCodeNameContaining(@Param("groupCode") String groupCode, @Param("codeName") String codeName);

    /**
     * 시스템 코드 목록 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.isSystem = true AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder")
    List<Code> findSystemCodes();

    /**
     * 코드 타입별 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.codeType = :codeType AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder")
    List<Code> findByCodeType(@Param("codeType") Code.CodeType codeType);

    /**
     * 회사별 코드 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc JOIN FETCH c.company comp WHERE c.company.id = :companyId AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder")
    List<Code> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 전사 공통 코드 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc WHERE c.company IS NULL AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder")
    List<Code> findGlobalCodes();

    /**
     * 그룹코드별 회사 코드 조회 (전사 공통 + 회사별)
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND (c.company IS NULL OR c.company.id = :companyId) AND c.isActive = true AND c.isDeleted = false ORDER BY c.sortOrder, c.codeValue")
    List<Code> findActiveByGroupCodeAndCompany(@Param("groupCode") String groupCode, @Param("companyId") Long companyId);

    /**
     * 특정 레벨의 코드 조회
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.level = :level AND c.isDeleted = false ORDER BY c.groupCode, c.sortOrder")
    List<Code> findByLevel(@Param("level") Integer level);

    /**
     * 그룹코드와 코드값 중복 확인
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.groupCode = :groupCode AND c.codeValue = :codeValue AND c.isDeleted = false")
    boolean existsByGroupCodeAndCodeValue(@Param("groupCode") String groupCode, @Param("codeValue") String codeValue);

    /**
     * 그룹코드와 코드값 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Code c WHERE c.groupCode = :groupCode AND c.codeValue = :codeValue AND c.id != :excludeId AND c.isDeleted = false")
    boolean existsByGroupCodeAndCodeValueAndIdNot(@Param("groupCode") String groupCode, @Param("codeValue") String codeValue, @Param("excludeId") Long excludeId);

    /**
     * 그룹코드 목록 조회 (중복 제거)
     */
    @Query("SELECT DISTINCT c.groupCode FROM Code c WHERE c.isDeleted = false ORDER BY c.groupCode")
    List<String> findDistinctGroupCodes();

    /**
     * 활성 그룹코드 목록 조회
     */
    @Query("SELECT DISTINCT c.groupCode FROM Code c WHERE c.isActive = true AND c.isDeleted = false ORDER BY c.groupCode")
    List<String> findDistinctActiveGroupCodes();

    /**
     * 전체 코드 목록 조회 (페이징, 연관관계 포함)
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.isDeleted = false")
    Page<Code> findAllWithParentAndCompany(Pageable pageable);

    /**
     * 그룹코드별 코드 검색 (페이징)
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE c.groupCode = :groupCode AND (c.codeValue LIKE %:searchTerm% OR c.codeName LIKE %:searchTerm%) AND c.isDeleted = false")
    Page<Code> searchByGroupCodeAndTerm(@Param("groupCode") String groupCode, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 코드 계층 구조 조회 (특정 코드의 모든 하위 코드)
     */
    @Query(value = "WITH RECURSIVE code_hierarchy AS (" +
                  "  SELECT id, group_code, code_value, code_name, parent_code_id, level " +
                  "  FROM codes " +
                  "  WHERE id = :codeId AND is_deleted = false " +
                  "  UNION ALL " +
                  "  SELECT c.id, c.group_code, c.code_value, c.code_name, c.parent_code_id, c.level " +
                  "  FROM codes c " +
                  "  INNER JOIN code_hierarchy ch ON c.parent_code_id = ch.id " +
                  "  WHERE c.is_deleted = false " +
                  ") " +
                  "SELECT id FROM code_hierarchy", 
           nativeQuery = true)
    List<Long> findCodeHierarchyIds(@Param("codeId") Long codeId);

    /**
     * 속성값으로 코드 검색
     */
    @Query("SELECT c FROM Code c LEFT JOIN FETCH c.parentCode pc LEFT JOIN FETCH c.company comp WHERE " +
           "(c.attribute1 = :attributeValue OR c.attribute2 = :attributeValue OR c.attribute3 = :attributeValue) " +
           "AND c.isDeleted = false")
    List<Code> findByAttributeValue(@Param("attributeValue") String attributeValue);

    /**
     * 코드 통계 조회 - 그룹코드별 개수
     */
    @Query("SELECT c.groupCode, COUNT(c) FROM Code c WHERE c.isDeleted = false GROUP BY c.groupCode ORDER BY c.groupCode")
    List<Object[]> getCodeCountByGroup();

    /**
     * 코드 통계 조회 - 레벨별 개수
     */
    @Query("SELECT c.level, COUNT(c) FROM Code c WHERE c.isDeleted = false GROUP BY c.level ORDER BY c.level")
    List<Object[]> getCodeCountByLevel();

    /**
     * 코드 통계 조회 - 타입별 개수
     */
    @Query("SELECT c.codeType, COUNT(c) FROM Code c WHERE c.isDeleted = false GROUP BY c.codeType")
    List<Object[]> getCodeCountByType();
}




