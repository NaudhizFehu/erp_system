package com.erp.sales.repository;

import com.erp.sales.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 견적 Repository
 * 견적 데이터 액세스를 담당합니다
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /**
     * 회사별 견적 조회 (페이징)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Quote> findByCompanyIdAndIsDeletedFalse(Long companyId, Pageable pageable);

    /**
     * 견적번호로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "quoteItems"})
    Optional<Quote> findByQuoteNumberAndIsDeletedFalse(String quoteNumber);

    /**
     * 회사별 견적번호 중복 확인
     */
    boolean existsByCompanyIdAndQuoteNumberAndIsDeletedFalse(Long companyId, String quoteNumber);

    /**
     * 고객별 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Quote> findByCustomerIdAndIsDeletedFalse(Long customerId, Pageable pageable);

    /**
     * 영업담당자별 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Quote> findBySalesRepIdAndIsDeletedFalse(Long salesRepId, Pageable pageable);

    /**
     * 견적 상태별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Quote> findByCompanyIdAndQuoteStatusAndIsDeletedFalse(Long companyId, Quote.QuoteStatus quoteStatus, Pageable pageable);

    /**
     * 견적 우선순위별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Quote> findByCompanyIdAndPriorityAndIsDeletedFalse(Long companyId, Quote.QuotePriority priority, Pageable pageable);

    /**
     * 견적 검색 (견적번호, 고객명, 제목)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q JOIN q.customer c WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND (LOWER(q.quoteNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Quote> searchQuotes(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q JOIN q.customer c WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(q.quoteNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:customerId IS NULL OR q.customer.id = :customerId) " +
           "AND (:quoteStatus IS NULL OR q.quoteStatus = :quoteStatus) " +
           "AND (:priority IS NULL OR q.priority = :priority) " +
           "AND (:salesRepId IS NULL OR q.salesRepId = :salesRepId)")
    Page<Quote> searchQuotesAdvanced(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            @Param("customerId") Long customerId,
            @Param("quoteStatus") Quote.QuoteStatus quoteStatus,
            @Param("priority") Quote.QuotePriority priority,
            @Param("salesRepId") Long salesRepId,
            Pageable pageable);

    /**
     * 견적일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND ((:quoteDateFrom IS NULL OR q.quoteDate >= :quoteDateFrom) " +
           "AND (:quoteDateTo IS NULL OR q.quoteDate <= :quoteDateTo))")
    Page<Quote> findByQuoteDateRange(
            @Param("companyId") Long companyId,
            @Param("quoteDateFrom") LocalDate quoteDateFrom,
            @Param("quoteDateTo") LocalDate quoteDateTo,
            Pageable pageable);

    /**
     * 유효기한 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND ((:validUntilFrom IS NULL OR q.validUntil >= :validUntilFrom) " +
           "AND (:validUntilTo IS NULL OR q.validUntil <= :validUntilTo))")
    Page<Quote> findByValidUntilRange(
            @Param("companyId") Long companyId,
            @Param("validUntilFrom") LocalDate validUntilFrom,
            @Param("validUntilTo") LocalDate validUntilTo,
            Pageable pageable);

    /**
     * 견적금액 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND ((:totalAmountFrom IS NULL OR q.totalAmount >= :totalAmountFrom) " +
           "AND (:totalAmountTo IS NULL OR q.totalAmount <= :totalAmountTo))")
    Page<Quote> findByTotalAmountRange(
            @Param("companyId") Long companyId,
            @Param("totalAmountFrom") BigDecimal totalAmountFrom,
            @Param("totalAmountTo") BigDecimal totalAmountTo,
            Pageable pageable);

    /**
     * 만료된 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.validUntil < :currentDate AND q.quoteStatus NOT IN ('EXPIRED', 'CONVERTED', 'CANCELLED')")
    List<Quote> findExpiredQuotes(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 만료 임박 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.validUntil BETWEEN :currentDate AND :expiryDate " +
           "AND q.quoteStatus IN ('SENT', 'VIEWED')")
    List<Quote> findExpiringSoonQuotes(
            @Param("companyId") Long companyId, 
            @Param("currentDate") LocalDate currentDate, 
            @Param("expiryDate") LocalDate expiryDate);

    /**
     * 전환 가능한 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteStatus = 'ACCEPTED' AND q.validUntil >= :currentDate")
    List<Quote> findConvertibleQuotes(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 발송 대기 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteStatus = 'DRAFT' AND SIZE(q.quoteItems) > 0")
    List<Quote> findQuotesReadyToSend(@Param("companyId") Long companyId);

    /**
     * 응답 대기 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteStatus IN ('SENT', 'VIEWED') AND q.validUntil >= :currentDate")
    List<Quote> findQuotesAwaitingResponse(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 태그로 견적 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.tags LIKE CONCAT('%', :tag, '%')")
    Page<Quote> findByTag(@Param("companyId") Long companyId, @Param("tag") String tag, Pageable pageable);

    /**
     * 회사별 견적 통계
     */
    @Query("SELECT " +
           "COUNT(q.id) as totalQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'DRAFT' THEN 1 END) as draftQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'SENT' THEN 1 END) as sentQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'ACCEPTED' THEN 1 END) as acceptedQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'REJECTED' THEN 1 END) as rejectedQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'EXPIRED' THEN 1 END) as expiredQuotes, " +
           "COUNT(CASE WHEN q.quoteStatus = 'CONVERTED' THEN 1 END) as convertedQuotes, " +
           "COALESCE(SUM(q.totalAmount), 0) as totalQuoteAmount, " +
           "COALESCE(AVG(q.totalAmount), 0) as averageQuoteAmount, " +
           "COALESCE(SUM(CASE WHEN q.quoteStatus = 'CONVERTED' THEN q.totalAmount END), 0) as totalConvertedAmount " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false")
    Object[] getQuoteStatistics(@Param("companyId") Long companyId);

    /**
     * 영업담당자별 견적 통계
     */
    @Query("SELECT q.salesRepId, q.salesRepName, COUNT(q.id), COALESCE(SUM(q.totalAmount), 0), " +
           "COUNT(CASE WHEN q.quoteStatus = 'CONVERTED' THEN 1 END) " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.salesRepId IS NOT NULL " +
           "GROUP BY q.salesRepId, q.salesRepName " +
           "ORDER BY SUM(q.totalAmount) DESC")
    List<Object[]> getQuoteStatsBySalesRep(@Param("companyId") Long companyId);

    /**
     * 견적 상태별 통계
     */
    @Query("SELECT q.quoteStatus, COUNT(q.id), COALESCE(SUM(q.totalAmount), 0) " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "GROUP BY q.quoteStatus " +
           "ORDER BY COUNT(q.id) DESC")
    List<Object[]> getQuoteStatsByStatus(@Param("companyId") Long companyId);

    /**
     * 월별 견적 통계
     */
    @Query("SELECT YEAR(q.quoteDate), MONTH(q.quoteDate), COUNT(q.id), COALESCE(SUM(q.totalAmount), 0), " +
           "COUNT(CASE WHEN q.quoteStatus = 'CONVERTED' THEN 1 END) " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteDate >= :fromDate " +
           "GROUP BY YEAR(q.quoteDate), MONTH(q.quoteDate) " +
           "ORDER BY YEAR(q.quoteDate), MONTH(q.quoteDate)")
    List<Object[]> getQuoteStatsByMonth(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 고객별 견적 통계
     */
    @Query("SELECT c.id, c.customerName, COUNT(q.id), COALESCE(SUM(q.totalAmount), 0), " +
           "COUNT(CASE WHEN q.quoteStatus = 'CONVERTED' THEN 1 END) " +
           "FROM Quote q JOIN q.customer c WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "GROUP BY c.id, c.customerName " +
           "ORDER BY SUM(q.totalAmount) DESC")
    List<Object[]> getQuoteStatsByCustomer(@Param("companyId") Long companyId);

    /**
     * 견적 전환율 계산
     */
    @Query("SELECT " +
           "(COUNT(CASE WHEN q.quoteStatus = 'CONVERTED' THEN 1 END) * 100.0 / " +
           " NULLIF(COUNT(CASE WHEN q.quoteStatus IN ('SENT', 'VIEWED', 'ACCEPTED', 'REJECTED', 'CONVERTED') THEN 1 END), 0)) as conversionRate " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteDate >= :fromDate")
    Double getConversionRate(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 견적 승인율 계산
     */
    @Query("SELECT " +
           "(COUNT(CASE WHEN q.quoteStatus = 'ACCEPTED' THEN 1 END) * 100.0 / " +
           " NULLIF(COUNT(CASE WHEN q.quoteStatus IN ('ACCEPTED', 'REJECTED') THEN 1 END), 0)) as acceptanceRate " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteDate >= :fromDate")
    Double getAcceptanceRate(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 평균 견적 처리 시간 (일)
     * FUNCTION 호출 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', 'DAY', q.sentDate, q.respondedDate)) " +
    //        "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
    //        "AND q.sentDate IS NOT NULL AND q.respondedDate IS NOT NULL " +
    //        "AND q.quoteDate >= :fromDate")
    // Double getAverageResponseDays(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 상위 견적 (금액 기준)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "ORDER BY q.totalAmount DESC")
    Page<Quote> findTopQuotesByAmount(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 견적
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "ORDER BY q.quoteDate DESC, q.createdAt DESC")
    Page<Quote> findRecentQuotes(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 오늘 견적
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteDate = :today")
    List<Quote> findTodayQuotes(@Param("companyId") Long companyId, @Param("today") LocalDate today);

    /**
     * 이번 주 견적
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.quoteDate >= :weekStart AND q.quoteDate <= :weekEnd")
    List<Quote> findThisWeekQuotes(
            @Param("companyId") Long companyId, 
            @Param("weekStart") LocalDate weekStart, 
            @Param("weekEnd") LocalDate weekEnd);

    /**
     * 이번 달 견적
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND YEAR(q.quoteDate) = :year AND MONTH(q.quoteDate) = :month")
    List<Quote> findThisMonthQuotes(@Param("companyId") Long companyId, @Param("year") int year, @Param("month") int month);

    /**
     * 우선순위별 견적 통계
     */
    @Query("SELECT q.priority, COUNT(q.id), COALESCE(SUM(q.totalAmount), 0) " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "GROUP BY q.priority " +
           "ORDER BY COUNT(q.id) DESC")
    List<Object[]> getQuoteStatsByPriority(@Param("companyId") Long companyId);

    /**
     * 견적 항목 수별 통계
     */
    @Query("SELECT SIZE(q.quoteItems), COUNT(q.id), COALESCE(AVG(q.totalAmount), 0) " +
           "FROM Quote q WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "GROUP BY SIZE(q.quoteItems) " +
           "ORDER BY SIZE(q.quoteItems)")
    List<Object[]> getQuoteStatsByItemCount(@Param("companyId") Long companyId);

    /**
     * 견적서 만료 상태 일괄 업데이트
     */
    @Query("UPDATE Quote q SET q.quoteStatus = 'EXPIRED' " +
           "WHERE q.company.id = :companyId AND q.isDeleted = false " +
           "AND q.validUntil < :currentDate " +
           "AND q.quoteStatus IN ('SENT', 'VIEWED')")
    void updateExpiredQuotes(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 고객의 최근 견적
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT q FROM Quote q WHERE q.customer.id = :customerId AND q.isDeleted = false " +
           "ORDER BY q.quoteDate DESC")
    Page<Quote> findRecentQuotesByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * 견적서 복사본 생성용 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "quoteItems"})
    @Query("SELECT q FROM Quote q WHERE q.id = :quoteId AND q.isDeleted = false")
    Optional<Quote> findForCopy(@Param("quoteId") Long quoteId);
}
