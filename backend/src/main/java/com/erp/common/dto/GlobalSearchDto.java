package com.erp.common.dto;

import java.util.List;

/**
 * 전역 검색 관련 DTO
 */
public class GlobalSearchDto {

    /**
     * 검색 결과 DTO
     */
    public record SearchResult(
            String id,
            String title,
            String description,
            String type,
            String url
    ) {}

    /**
     * 검색 요청 DTO
     */
    public record SearchRequest(
            String query,
            Long companyId,
            String[] types
    ) {}

    /**
     * 검색 응답 DTO
     */
    public record SearchResponse(
            List<SearchResult> results,
            int totalCount,
            long searchTime
    ) {}
}
