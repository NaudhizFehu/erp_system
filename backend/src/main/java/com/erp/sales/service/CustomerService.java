package com.erp.sales.service;

import com.erp.sales.dto.CustomerDto;
import com.erp.sales.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 고객 관리 서비스 인터페이스
 * 고객 관련 비즈니스 로직을 정의합니다
 */
public interface CustomerService {

    /**
     * 고객 생성
     */
    CustomerDto.CustomerResponseDto createCustomer(CustomerDto.CustomerCreateDto createDto);

    /**
     * 고객 수정
     */
    CustomerDto.CustomerResponseDto updateCustomer(Long customerId, CustomerDto.CustomerUpdateDto updateDto);

    /**
     * 고객 삭제 (소프트 삭제)
     */
    void deleteCustomer(Long customerId);

    /**
     * 고객 상세 조회
     */
    CustomerDto.CustomerResponseDto getCustomer(Long customerId);

    /**
     * 고객코드로 조회
     */
    CustomerDto.CustomerResponseDto getCustomerByCode(String customerCode);

    /**
     * 회사별 고객 목록 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersByCompany(Long companyId, Pageable pageable);

    /**
     * 고객 검색
     */
    Page<CustomerDto.CustomerSummaryDto> searchCustomers(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    Page<CustomerDto.CustomerSummaryDto> searchCustomersAdvanced(Long companyId, CustomerDto.CustomerSearchDto searchDto, Pageable pageable);

    /**
     * 영업담당자별 고객 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersBySalesManager(Long companyId, Long salesManagerId, Pageable pageable);

    /**
     * 고객 유형별 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersByType(Long companyId, String customerType, Pageable pageable);

    /**
     * 고객 상태별 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersByStatus(Long companyId, Customer.CustomerStatus status, Pageable pageable);

    /**
     * 고객 등급별 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersByGrade(Long companyId, Customer.CustomerGrade grade, Pageable pageable);


    /**
     * VIP 고객 조회
     */
    List<CustomerDto.CustomerSummaryDto> getVipCustomers(Long companyId);

    /**
     * 휴면 고객 조회
     */
    List<CustomerDto.CustomerSummaryDto> getDormantCustomers(Long companyId, int dormantDays);

    /**
     * 미수금 있는 고객 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getCustomersWithOutstanding(Long companyId, Pageable pageable);

    /**
     * 신용한도 초과 고객 조회
     */
    List<CustomerDto.CustomerSummaryDto> getCustomersOverCreditLimit(Long companyId);

    /**
     * 상위 고객 조회 (주문금액 기준)
     */
    Page<CustomerDto.CustomerSummaryDto> getTopCustomers(Long companyId, Pageable pageable);

    /**
     * 최근 연락한 고객 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getRecentlyContactedCustomers(Long companyId, Pageable pageable);

    /**
     * 최근 주문한 고객 조회
     */
    Page<CustomerDto.CustomerSummaryDto> getRecentlyOrderedCustomers(Long companyId, Pageable pageable);

    /**
     * 고객 통계 조회
     */
    CustomerDto.CustomerStatsDto getCustomerStatistics(Long companyId);

    /**
     * 고객별 거래내역 조회
     */
    List<CustomerDto.CustomerTransactionDto> getCustomerTransactions(Long customerId);

    /**
     * 고객 연락처 업데이트
     */
    CustomerDto.CustomerResponseDto updateCustomerContact(Long customerId, CustomerDto.CustomerContactUpdateDto contactDto);

    /**
     * 고객 주소 업데이트
     */
    CustomerDto.CustomerResponseDto updateCustomerAddress(Long customerId, CustomerDto.CustomerAddressUpdateDto addressDto);

    /**
     * 고객 거래조건 업데이트 (PaymentTerm 관련 필드들은 Customer 엔티티에 없음)
     */
    CustomerDto.CustomerResponseDto updateCustomerTerms(Long customerId, CustomerDto.CustomerTermsUpdateDto termsDto);


    /**
     * 고객 상태 변경
     */
    void changeCustomerStatus(Long customerId, CustomerDto.CustomerStatusChangeDto statusChangeDto);

    /**
     * 고객 등급 변경
     */
    void changeCustomerGrade(Long customerId, CustomerDto.CustomerGradeChangeDto gradeChangeDto);

    /**
     * 고객 활성화/비활성화
     */
    CustomerDto.CustomerResponseDto toggleCustomerActive(Long customerId);

    /**
     * 고객코드 중복 확인
     */
    boolean isCustomerCodeDuplicate(Long companyId, String customerCode, Long excludeCustomerId);

    /**
     * 사업자등록번호 중복 확인
     */
    boolean isBusinessRegistrationNumberDuplicate(String businessRegistrationNumber, Long excludeCustomerId);

    /**
     * 고객 주문 통계 업데이트
     */
    void updateCustomerOrderStatistics(Long customerId);

    /**
     * 모든 고객 주문 통계 일괄 업데이트
     */
    void updateAllCustomerOrderStatistics(Long companyId);

    /**
     * 고객 등급 자동 업데이트 (주문금액 기준)
     */

    /**
     * 휴면 고객 자동 전환
     */
    int convertDormantCustomers(Long companyId, int dormantDays);

    /**
     * 고객 생일 알림 대상 조회
     */
    List<CustomerDto.CustomerSummaryDto> getCustomersWithBirthdayThisMonth(Long companyId);

    /**
     * 영업담당자별 고객 통계
     */
    List<Object[]> getCustomerStatsBySalesManager(Long companyId);

    /**
     * 고객 유형별 통계
     */
    List<Object[]> getCustomerStatsByType(Long companyId);


    /**
     * 지역별 고객 분포
     */
    List<Object[]> getCustomerDistributionByCity(Long companyId);

    /**
     * 월별 신규 고객 통계
     */
    List<Object[]> getNewCustomersByMonth(Long companyId, LocalDate fromDate);

    /**
     * 고객 세그멘테이션 분석
     */
    List<Object[]> getCustomerSegmentationAnalysis(Long companyId);

    /**
     * 고객 라이프타임 가치 계산
     */
    BigDecimal calculateCustomerLifetimeValue(Long customerId);

    /**
     * 고객 재구매율 계산
     */
    Double calculateCustomerRepeatPurchaseRate(Long companyId, LocalDate fromDate);

    /**
     * 고객 이탈 위험도 분석
     */
    List<CustomerDto.CustomerSummaryDto> getChurnRiskCustomers(Long companyId);
}




