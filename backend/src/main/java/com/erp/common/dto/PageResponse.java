package com.erp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * òt’ Qı DTO
 * òt’ ò¨ pt0 ©]¸ T¿pt0| Ïhi»‰
 *
 * @param <T> XP  ¿Ö
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * òt¿ XP  ©]
     */
    private List<T> content;

    /**
     * ¨ òt¿ à8 (0Ä0 ‹ë)
     */
    private int page;

    /**
     * òt¿ l0
     */
    private int size;

    /**
     * ¥ îå 
     */
    private long totalElements;

    /**
     * ¥ òt¿ 
     */
    private int totalPages;

    /**
     * ´ òt¿ ÏÄ
     */
    private boolean first;

    /**
     * »¿… òt¿ ÏÄ
     */
    private boolean last;
}
