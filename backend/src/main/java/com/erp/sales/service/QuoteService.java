package com.erp.sales.service;

import com.erp.sales.dto.QuoteDto;
import com.erp.sales.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * 견적 서비스 인터페이스 (기본 구현)
 * 견적 관련 기본적인 비즈니스 로직을 정의합니다
 */
public interface QuoteService {

    /**
     * 견적 생성
     */
    QuoteDto.QuoteResponseDto createQuote(QuoteDto.QuoteCreateDto createDto);

    /**
     * 견적 수정
     */
    QuoteDto.QuoteResponseDto updateQuote(Long quoteId, QuoteDto.QuoteUpdateDto updateDto);

    /**
     * 견적 삭제
     */
    void deleteQuote(Long quoteId);

    /**
     * 견적 조회
     */
    QuoteDto.QuoteResponseDto getQuote(Long quoteId);

    /**
     * 견적 번호로 조회
     */
    QuoteDto.QuoteResponseDto getQuoteByNumber(String quoteNumber);

    /**
     * 전체 견적 목록 조회
     */
    Page<QuoteDto.QuoteSummaryDto> getAllQuotes(Pageable pageable);

    /**
     * 고객별 견적 목록 조회
     */
    Page<QuoteDto.QuoteSummaryDto> getQuotesByCustomer(Long customerId, Pageable pageable);

    /**
     * 상태별 견적 목록 조회
     */
    Page<QuoteDto.QuoteSummaryDto> getQuotesByStatus(Quote.QuoteStatus status, Pageable pageable);

    /**
     * 영업담당자별 견적 목록 조회
     */
    Page<QuoteDto.QuoteSummaryDto> getQuotesBySalesRep(Long salesRepId, Pageable pageable);

    /**
     * 견적 검색
     */
    Page<QuoteDto.QuoteSummaryDto> searchQuotes(QuoteDto.QuoteSearchDto searchDto, Pageable pageable);

    /**
     * 만료 예정 견적 조회
     */
    List<QuoteDto.QuoteSummaryDto> getExpiringQuotes(int days);

    /**
     * 견적 상태 변경
     */
    QuoteDto.QuoteResponseDto changeQuoteStatus(Long quoteId, QuoteDto.QuoteStatusChangeDto statusChangeDto);

    /**
     * 견적서 발송
     */
    QuoteDto.QuoteResponseDto sendQuote(Long quoteId, QuoteDto.QuoteSendDto sendDto);

    /**
     * 견적 응답 처리
     */
    QuoteDto.QuoteResponseDto respondToQuote(Long quoteId, QuoteDto.QuoteApprovalDto approvalDto);

    /**
     * 견적을 주문으로 전환
     */
    Long convertQuoteToOrder(Long quoteId, QuoteDto.QuoteToOrderDto conversionDto);

    /**
     * 견적 복사
     */
    QuoteDto.QuoteResponseDto copyQuote(Long quoteId, QuoteDto.QuoteCopyDto copyDto);

    /**
     * 견적 통계
     */
    QuoteDto.QuoteStatsDto getQuoteStatistics(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 상태별 견적 수 통계
     */
    List<Object[]> getQuoteCountByStatus();

    /**
     * 고객별 견적 수 통계
     */
    List<Object[]> getQuoteCountByCustomer();

    /**
     * 영업담당자별 견적 수 통계
     */
    List<Object[]> getQuoteCountBySalesRep();
}