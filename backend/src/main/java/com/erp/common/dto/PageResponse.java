package com.erp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 페이지네이션 응답 DTO
 * 페이지네이션 처리된 데이터를 반환할 때 사용합니다
 *
 * @param <T> 컨텐츠 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 페이지 컨텐츠 목록
     */
    private List<T> content;

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int page;

    /**
     * 페이지 크기
     */
    private int size;

    /**
     * 전체 요소 수
     */
    private long totalElements;

    /**
     * 전체 페이지 수
     */
    private int totalPages;

    /**
     * 첫 페이지 여부
     */
    private boolean first;

    /**
     * 마지막 페이지 여부
     */
    private boolean last;
}
