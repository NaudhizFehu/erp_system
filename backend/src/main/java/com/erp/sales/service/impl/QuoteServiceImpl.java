package com.erp.sales.service.impl;

import com.erp.sales.dto.QuoteDto;
import com.erp.sales.entity.Quote;
import com.erp.sales.repository.QuoteRepository;
import com.erp.sales.service.QuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * 견적 서비스 구현체 (기본 구현)
 * TODO: 실제 비즈니스 로직 구현 필요
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto createQuote(QuoteDto.QuoteCreateDto createDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto updateQuote(Long quoteId, QuoteDto.QuoteUpdateDto updateDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public void deleteQuote(Long quoteId) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public QuoteDto.QuoteResponseDto getQuote(Long quoteId) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public QuoteDto.QuoteResponseDto getQuoteByNumber(String quoteNumber) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public Page<QuoteDto.QuoteSummaryDto> getAllQuotes(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<QuoteDto.QuoteSummaryDto> getQuotesByCustomer(Long customerId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<QuoteDto.QuoteSummaryDto> getQuotesByStatus(Quote.QuoteStatus status, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<QuoteDto.QuoteSummaryDto> getQuotesBySalesRep(Long salesRepId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<QuoteDto.QuoteSummaryDto> searchQuotes(QuoteDto.QuoteSearchDto searchDto, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public List<QuoteDto.QuoteSummaryDto> getExpiringQuotes(int days) {
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto changeQuoteStatus(Long quoteId, QuoteDto.QuoteStatusChangeDto statusChangeDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto sendQuote(Long quoteId, QuoteDto.QuoteSendDto sendDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto respondToQuote(Long quoteId, QuoteDto.QuoteApprovalDto approvalDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public Long convertQuoteToOrder(Long quoteId, QuoteDto.QuoteToOrderDto conversionDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public QuoteDto.QuoteResponseDto copyQuote(Long quoteId, QuoteDto.QuoteCopyDto copyDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public QuoteDto.QuoteStatsDto getQuoteStatistics(Long companyId, LocalDate startDate, LocalDate endDate) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public List<Object[]> getQuoteCountByStatus() {
        return new ArrayList<>();
    }

    @Override
    public List<Object[]> getQuoteCountByCustomer() {
        return new ArrayList<>();
    }

    @Override
    public List<Object[]> getQuoteCountBySalesRep() {
        return new ArrayList<>();
    }
}