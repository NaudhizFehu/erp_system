package com.erp.sales.service.impl;

import com.erp.common.exception.BusinessException;
import com.erp.common.exception.EntityNotFoundException;
import com.erp.common.utils.ExceptionUtils;
import com.erp.sales.dto.CustomerDto;
import com.erp.sales.entity.Customer;
import com.erp.sales.repository.CustomerRepository;
import com.erp.sales.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 고객 관리 서비스 구현
 * 고객 관련 비즈니스 로직을 처리합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto createCustomer(CustomerDto.CustomerCreateDto createDto) {
        log.info("고객 생성 요청: {}", createDto.customerCode());
        
        // 고객코드 중복 확인
        if (customerRepository.existsByCompanyIdAndCustomerCodeAndIsDeletedFalse(createDto.companyId(), createDto.customerCode())) {
            ExceptionUtils.throwDuplicate("이미 존재하는 고객코드입니다: " + createDto.customerCode());
        }

        // 사업자등록번호 중복 확인 (있는 경우)
        if (createDto.businessRegistrationNumber() != null && !createDto.businessRegistrationNumber().trim().isEmpty()) {
            customerRepository.findByBusinessRegistrationNumberAndIsDeletedFalse(createDto.businessRegistrationNumber())
                    .ifPresent(existing -> ExceptionUtils.throwDuplicate("이미 존재하는 사업자등록번호입니다: " + createDto.businessRegistrationNumber()));
        }

        // Customer 엔티티 생성
        Customer customer = createCustomerEntity(createDto);
        customer = customerRepository.save(customer);

        log.info("고객 생성 완료: ID={}, 코드={}", customer.getId(), customer.getCustomerCode());
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomer(Long customerId, CustomerDto.CustomerUpdateDto updateDto) {
        log.info("고객 수정 요청: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        // 사업자등록번호 중복 확인 (변경하는 경우)
        if (updateDto.businessRegistrationNumber() != null && !updateDto.businessRegistrationNumber().trim().isEmpty()) {
            customerRepository.findByBusinessRegistrationNumberAndIsDeletedFalse(updateDto.businessRegistrationNumber())
                    .filter(existing -> !existing.getId().equals(customerId))
                    .ifPresent(existing -> ExceptionUtils.throwDuplicate("이미 존재하는 사업자등록번호입니다: " + updateDto.businessRegistrationNumber()));
        }

        // Customer 엔티티 업데이트
        updateCustomerEntity(customer, updateDto);
        customer = customerRepository.save(customer);

        log.info("고객 수정 완료: ID={}, 코드={}", customer.getId(), customer.getCustomerCode());
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("고객 삭제 요청: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        // 미수금이 있는 경우 삭제 불가
        if (customer.getOutstandingAmount() != null && customer.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0) {
            ExceptionUtils.throwBusinessException("미수금이 있는 고객은 삭제할 수 없습니다");
        }

        customer.softDelete(null);
        customerRepository.save(customer);

        log.info("고객 삭제 완료: ID={}, 코드={}", customer.getId(), customer.getCustomerCode());
    }

    @Override
    public CustomerDto.CustomerResponseDto getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));
        
        return mapToResponseDto(customer);
    }

    @Override
    public CustomerDto.CustomerResponseDto getCustomerByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCodeAndIsDeletedFalse(customerCode)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerCode));
        
        return mapToResponseDto(customer);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByCompany(Long companyId, Pageable pageable) {
        return customerRepository.findByCompanyIdAndIsDeletedFalse(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> searchCustomers(Long companyId, String searchTerm, Pageable pageable) {
        return customerRepository.searchCustomers(companyId, searchTerm, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> searchCustomersAdvanced(Long companyId, CustomerDto.CustomerSearchDto searchDto, Pageable pageable) {
        return customerRepository.searchCustomersAdvanced(
                companyId,
                searchDto.searchTerm(),
                searchDto.customerType(),
                searchDto.customerStatus(),
                searchDto.customerGrade(),
                searchDto.isActive(),
                searchDto.salesManagerId(),
                searchDto.city(),
                searchDto.businessType(),
                pageable
        ).map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersBySalesManager(Long salesManagerId, Pageable pageable) {
        return customerRepository.findBySalesManagerIdAndIsDeletedFalse(salesManagerId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByType(Long companyId, String customerType, Pageable pageable) {
        Customer.CustomerType type = Customer.CustomerType.valueOf(customerType);
        return customerRepository.findByCompanyIdAndCustomerTypeAndIsDeletedFalse(companyId, type, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByStatus(Long companyId, String customerStatus, Pageable pageable) {
        Customer.CustomerStatus status = Customer.CustomerStatus.valueOf(customerStatus);
        return customerRepository.findByCompanyIdAndCustomerStatusAndIsDeletedFalse(companyId, status, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByGrade(Long companyId, String customerGrade, Pageable pageable) {
        Customer.CustomerGrade grade = Customer.CustomerGrade.valueOf(customerGrade);
        return customerRepository.findByCompanyIdAndCustomerGradeAndIsDeletedFalse(companyId, grade, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getVipCustomers(Long companyId) {
        return customerRepository.findVipCustomers(companyId)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getDormantCustomers(Long companyId, int dormantDays) {
        LocalDate cutoffDate = LocalDate.now().minusDays(dormantDays);
        return customerRepository.findDormantCustomers(companyId, cutoffDate)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersWithOutstanding(Long companyId, Pageable pageable) {
        return customerRepository.findCustomersWithOutstanding(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersOverCreditLimit(Long companyId, Pageable pageable) {
        return customerRepository.findCustomersOverCreditLimit(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getTopCustomers(Long companyId, Pageable pageable) {
        return customerRepository.findTopCustomersByOrderAmount(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getRecentlyContactedCustomers(Long companyId, Pageable pageable) {
        return customerRepository.findRecentlyContactedCustomers(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getRecentlyOrderedCustomers(Long companyId, Pageable pageable) {
        return customerRepository.findRecentlyOrderedCustomers(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public CustomerDto.CustomerStatsDto getCustomerStatistics(Long companyId) {
        Object[] stats = customerRepository.getCustomerStatistics(companyId);
        
        return new CustomerDto.CustomerStatsDto(
                ((Number) stats[0]).longValue(),  // totalCustomers
                ((Number) stats[1]).longValue(),  // activeCustomers
                ((Number) stats[2]).longValue(),  // inactiveCustomers
                ((Number) stats[3]).longValue(),  // vipCustomers
                ((Number) stats[4]).longValue(),  // prospectCustomers
                ((Number) stats[5]).longValue(),  // dormantCustomers
                (BigDecimal) stats[6],            // totalSalesAmount
                (BigDecimal) stats[7],            // averageSalesAmount
                (BigDecimal) stats[8],            // totalOutstandingAmount
                ((Number) stats[9]).longValue(),  // customersWithOutstanding
                ((Number) stats[10]).longValue(), // customersOverCreditLimit
                calculateAverageOrdersPerCustomer(companyId), // averageOrdersPerCustomer
                (BigDecimal) stats[7]             // averageOrderAmount (재사용)
        );
    }

    @Override
    public List<CustomerDto.CustomerTransactionDto> getCustomerTransactions(Long customerId) {
        // TODO: 주문, 결제, 견적 등의 거래내역을 조회하여 반환
        // 현재는 빈 리스트 반환 (실제 구현시 OrderRepository, PaymentRepository 등을 활용)
        return List.of();
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomerContact(Long customerId, CustomerDto.CustomerContactUpdateDto contactDto) {
        log.info("고객 연락처 업데이트: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        customer.setPhoneNumber(contactDto.phoneNumber());
        customer.setFaxNumber(contactDto.faxNumber());
        customer.setEmail(contactDto.email());
        customer.setWebsite(contactDto.website());
        customer.setLastContactDate(contactDto.lastContactDate());

        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomerAddress(Long customerId, CustomerDto.CustomerAddressUpdateDto addressDto) {
        log.info("고객 주소 업데이트: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        customer.setPostalCode(addressDto.postalCode());
        customer.setAddress(addressDto.address());
        customer.setAddressDetail(addressDto.addressDetail());
        customer.setCity(addressDto.city());
        customer.setDistrict(addressDto.district());
        customer.setCountry(addressDto.country());

        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomerTerms(Long customerId, CustomerDto.CustomerTermsUpdateDto termsDto) {
        log.info("고객 거래조건 업데이트: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        customer.setPaymentTerm(termsDto.paymentTerm());
        customer.setCustomPaymentDays(termsDto.customPaymentDays());
        customer.setCreditLimit(termsDto.creditLimit());
        customer.setDiscountRate(termsDto.discountRate());
        customer.setTaxRate(termsDto.taxRate());

        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto changeCustomerGrade(Long customerId, CustomerDto.CustomerGradeChangeDto gradeChangeDto) {
        log.info("고객 등급 변경: ID={}, 새등급={}", customerId, gradeChangeDto.customerGrade());
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        Customer.CustomerGrade oldGrade = customer.getCustomerGrade();
        customer.setCustomerGrade(gradeChangeDto.customerGrade());

        // 등급 변경 로그 (메타데이터에 기록)
        String gradeChangeLog = String.format("등급변경: %s → %s (%s) [%s]", 
                oldGrade, gradeChangeDto.customerGrade(), 
                gradeChangeDto.reason(), LocalDate.now());
        
        String metadata = customer.getMetadata();
        customer.setMetadata(metadata != null ? metadata + "\n" + gradeChangeLog : gradeChangeLog);

        customer = customerRepository.save(customer);
        log.info("고객 등급 변경 완료: ID={}, {}→{}", customerId, oldGrade, gradeChangeDto.customerGrade());
        
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto changeCustomerStatus(Long customerId, CustomerDto.CustomerStatusChangeDto statusChangeDto) {
        log.info("고객 상태 변경: ID={}, 새상태={}", customerId, statusChangeDto.customerStatus());
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        Customer.CustomerStatus oldStatus = customer.getCustomerStatus();
        customer.setCustomerStatus(statusChangeDto.customerStatus());

        // 상태 변경에 따른 추가 처리
        if (statusChangeDto.customerStatus() == Customer.CustomerStatus.DORMANT) {
            customer.setIsActive(false);
        } else if (statusChangeDto.customerStatus() == Customer.CustomerStatus.ACTIVE || 
                   statusChangeDto.customerStatus() == Customer.CustomerStatus.VIP) {
            customer.setIsActive(true);
        }

        // 상태 변경 로그 (메타데이터에 기록)
        String statusChangeLog = String.format("상태변경: %s → %s (%s) [%s]", 
                oldStatus, statusChangeDto.customerStatus(), 
                statusChangeDto.reason(), LocalDate.now());
        
        String metadata = customer.getMetadata();
        customer.setMetadata(metadata != null ? metadata + "\n" + statusChangeLog : statusChangeLog);

        customer = customerRepository.save(customer);
        log.info("고객 상태 변경 완료: ID={}, {}→{}", customerId, oldStatus, statusChangeDto.customerStatus());
        
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto toggleCustomerActive(Long customerId) {
        log.info("고객 활성화 토글: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        customer.setIsActive(!customer.getIsActive());
        
        // 비활성화시 상태도 변경
        if (!customer.getIsActive() && customer.getCustomerStatus() == Customer.CustomerStatus.ACTIVE) {
            customer.setCustomerStatus(Customer.CustomerStatus.INACTIVE);
        }

        customer = customerRepository.save(customer);
        log.info("고객 활성화 토글 완료: ID={}, 활성화={}", customerId, customer.getIsActive());
        
        return mapToResponseDto(customer);
    }

    @Override
    public boolean isCustomerCodeDuplicate(Long companyId, String customerCode, Long excludeCustomerId) {
        if (excludeCustomerId != null) {
            return customerRepository.existsByCompanyIdAndCustomerCodeAndIdNotAndIsDeletedFalse(companyId, customerCode, excludeCustomerId);
        } else {
            return customerRepository.existsByCompanyIdAndCustomerCodeAndIsDeletedFalse(companyId, customerCode);
        }
    }

    @Override
    public boolean isBusinessRegistrationNumberDuplicate(String businessRegistrationNumber, Long excludeCustomerId) {
        return customerRepository.findByBusinessRegistrationNumberAndIsDeletedFalse(businessRegistrationNumber)
                .filter(customer -> excludeCustomerId == null || !customer.getId().equals(excludeCustomerId))
                .isPresent();
    }

    @Override
    @Transactional
    public void updateCustomerOrderStatistics(Long customerId) {
        log.info("고객 주문 통계 업데이트: ID={}", customerId);
        customerRepository.updateCustomerOrderStatistics(customerId);
        
        // 평균 주문 금액 재계산
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.updateAverageOrderAmount();
            customerRepository.save(customer);
        }
    }

    @Override
    @Transactional
    public void updateAllCustomerOrderStatistics(Long companyId) {
        log.info("모든 고객 주문 통계 일괄 업데이트: companyId={}", companyId);
        customerRepository.updateAllCustomerOrderStatistics(companyId);
    }

    @Override
    @Transactional
    public void updateCustomerGradesBasedOnOrderAmount(Long companyId) {
        log.info("주문금액 기준 고객 등급 자동 업데이트: companyId={}", companyId);
        
        List<Customer> customers = customerRepository.findByCompanyIdAndIsActiveTrueAndIsDeletedFalse(companyId);
        
        for (Customer customer : customers) {
            BigDecimal totalAmount = customer.getTotalOrderAmount();
            if (totalAmount == null) totalAmount = BigDecimal.ZERO;
            
            Customer.CustomerGrade newGrade = determineGradeByOrderAmount(totalAmount);
            if (newGrade != customer.getCustomerGrade()) {
                customer.setCustomerGrade(newGrade);
                customerRepository.save(customer);
                log.info("고객 등급 자동 업데이트: ID={}, 등급={}, 주문금액={}", 
                        customer.getId(), newGrade, totalAmount);
            }
        }
    }

    @Override
    @Transactional
    public int convertDormantCustomers(Long companyId, int dormantDays) {
        log.info("휴면 고객 자동 전환: companyId={}, 기준일수={}", companyId, dormantDays);
        
        LocalDate cutoffDate = LocalDate.now().minusDays(dormantDays);
        List<Customer> dormantCustomers = customerRepository.findDormantCustomers(companyId, cutoffDate);
        
        int convertedCount = 0;
        for (Customer customer : dormantCustomers) {
            if (customer.getCustomerStatus() != Customer.CustomerStatus.DORMANT) {
                customer.setCustomerStatus(Customer.CustomerStatus.DORMANT);
                customer.setIsActive(false);
                customerRepository.save(customer);
                convertedCount++;
            }
        }
        
        log.info("휴면 고객 전환 완료: {}명", convertedCount);
        return convertedCount;
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getCustomersWithBirthdayThisMonth(Long companyId) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
        return customerRepository.findCustomersWithBirthdayInMonth(companyId, currentMonth)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getCustomerStatsBySalesManager(Long companyId) {
        return customerRepository.getCustomerCountBySalesManager(companyId);
    }

    @Override
    public List<Object[]> getCustomerStatsByType(Long companyId) {
        return customerRepository.getCustomerStatsByType(companyId);
    }

    @Override
    public List<Object[]> getCustomerStatsByGrade(Long companyId) {
        return customerRepository.getCustomerStatsByGrade(companyId);
    }

    @Override
    public List<Object[]> getCustomerDistributionByCity(Long companyId) {
        return customerRepository.getCustomerDistributionByCity(companyId);
    }

    @Override
    public List<Object[]> getNewCustomersByMonth(Long companyId, LocalDate fromDate) {
        return customerRepository.getNewCustomersByMonth(companyId, fromDate);
    }

    @Override
    public List<Object[]> getCustomerSegmentationAnalysis(Long companyId) {
        // RFM 분석 등의 고급 세그멘테이션 로직 구현
        // 현재는 기본 통계 반환
        return getCustomerStatsByGrade(companyId);
    }

    @Override
    public BigDecimal calculateCustomerLifetimeValue(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getTotalOrderAmount() == null || customer.getTotalOrderCount() == null) {
            return BigDecimal.ZERO;
        }
        
        // 간단한 CLV 계산: 평균 주문 금액 × 예상 주문 횟수
        BigDecimal avgOrderAmount = customer.getAverageOrderAmount();
        int orderFrequency = customer.getTotalOrderCount();
        
        // 예상 라이프타임 (년) - 고객 등급에 따라 다르게 설정
        BigDecimal expectedLifetime = switch (customer.getCustomerGrade()) {
            case PLATINUM -> new BigDecimal("5");
            case GOLD -> new BigDecimal("4");
            case SILVER -> new BigDecimal("3");
            case BRONZE -> new BigDecimal("2");
            case GENERAL -> new BigDecimal("1");
        };
        
        return avgOrderAmount.multiply(new BigDecimal(orderFrequency)).multiply(expectedLifetime);
    }

    @Override
    public Double calculateCustomerRepeatPurchaseRate(Long companyId, LocalDate fromDate) {
        Long totalCustomers = customerRepository.countByCompanyIdAndIsDeletedFalse(companyId);
        Long repeatCustomers = customerRepository.getReorderCustomerCount(companyId);
        
        if (totalCustomers == 0) return 0.0;
        return repeatCustomers.doubleValue() / totalCustomers.doubleValue() * 100.0;
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getChurnRiskCustomers(Long companyId) {
        // 이탈 위험 고객: 최근 90일간 주문이 없고, 과거에는 주문이 있었던 고객
        LocalDate cutoffDate = LocalDate.now().minusDays(90);
        return customerRepository.findDormantCustomers(companyId, cutoffDate)
                .stream()
                .filter(customer -> customer.getTotalOrderCount() != null && customer.getTotalOrderCount() > 0)
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private Customer createCustomerEntity(CustomerDto.CustomerCreateDto createDto) {
        Customer customer = new Customer();
        
        customer.setCustomerCode(createDto.customerCode());
        customer.setCustomerName(createDto.customerName());
        customer.setCustomerNameEn(createDto.customerNameEn());
        // Customer 엔티티는 company 객체를 사용하므로 companyId 직접 설정 불가
        // company 객체는 createCustomer 메서드에서 설정됨
        customer.setCustomerType(createDto.customerType());
        customer.setCustomerStatus(createDto.customerStatus());
        customer.setCustomerGrade(createDto.customerGrade());
        customer.setIsActive(createDto.isActive());
        
        // 사업자 정보
        customer.setBusinessRegistrationNumber(createDto.businessRegistrationNumber());
        customer.setRepresentativeName(createDto.representativeName());
        customer.setBusinessType(createDto.businessType());
        customer.setBusinessItem(createDto.businessItem());
        
        // 연락처 정보
        customer.setPhoneNumber(createDto.phoneNumber());
        customer.setFaxNumber(createDto.faxNumber());
        customer.setEmail(createDto.email());
        customer.setWebsite(createDto.website());
        
        // 주소 정보
        customer.setPostalCode(createDto.postalCode());
        customer.setAddress(createDto.address());
        customer.setAddressDetail(createDto.addressDetail());
        customer.setCity(createDto.city());
        customer.setDistrict(createDto.district());
        customer.setCountry(createDto.country());
        
        // 영업 관리 정보
        customer.setSalesManagerId(createDto.salesManagerId());
        customer.setSalesManagerName(createDto.salesManagerName());
        customer.setFirstContactDate(createDto.firstContactDate());
        customer.setDescription(createDto.description());
        
        // 거래 조건
        customer.setPaymentTerm(createDto.paymentTerm());
        customer.setCustomPaymentDays(createDto.customPaymentDays());
        customer.setCreditLimit(createDto.creditLimit());
        customer.setDiscountRate(createDto.discountRate());
        customer.setTaxRate(createDto.taxRate());
        
        // 추가 정보
        customer.setTags(createDto.tags());
        customer.setSortOrder(createDto.sortOrder());
        customer.setMetadata(createDto.metadata());
        
        return customer;
    }

    private void updateCustomerEntity(Customer customer, CustomerDto.CustomerUpdateDto updateDto) {
        if (updateDto.customerName() != null) customer.setCustomerName(updateDto.customerName());
        if (updateDto.customerNameEn() != null) customer.setCustomerNameEn(updateDto.customerNameEn());
        if (updateDto.customerType() != null) customer.setCustomerType(updateDto.customerType());
        if (updateDto.customerStatus() != null) customer.setCustomerStatus(updateDto.customerStatus());
        if (updateDto.customerGrade() != null) customer.setCustomerGrade(updateDto.customerGrade());
        if (updateDto.isActive() != null) customer.setIsActive(updateDto.isActive());
        
        // 사업자 정보
        if (updateDto.representativeName() != null) customer.setRepresentativeName(updateDto.representativeName());
        if (updateDto.businessType() != null) customer.setBusinessType(updateDto.businessType());
        if (updateDto.businessItem() != null) customer.setBusinessItem(updateDto.businessItem());
        
        // 연락처 정보
        if (updateDto.phoneNumber() != null) customer.setPhoneNumber(updateDto.phoneNumber());
        if (updateDto.faxNumber() != null) customer.setFaxNumber(updateDto.faxNumber());
        if (updateDto.email() != null) customer.setEmail(updateDto.email());
        if (updateDto.website() != null) customer.setWebsite(updateDto.website());
        
        // 주소 정보
        if (updateDto.postalCode() != null) customer.setPostalCode(updateDto.postalCode());
        if (updateDto.address() != null) customer.setAddress(updateDto.address());
        if (updateDto.addressDetail() != null) customer.setAddressDetail(updateDto.addressDetail());
        if (updateDto.city() != null) customer.setCity(updateDto.city());
        if (updateDto.district() != null) customer.setDistrict(updateDto.district());
        if (updateDto.country() != null) customer.setCountry(updateDto.country());
        
        // 영업 관리 정보
        if (updateDto.salesManagerId() != null) customer.setSalesManagerId(updateDto.salesManagerId());
        if (updateDto.salesManagerName() != null) customer.setSalesManagerName(updateDto.salesManagerName());
        if (updateDto.lastContactDate() != null) customer.setLastContactDate(updateDto.lastContactDate());
        if (updateDto.description() != null) customer.setDescription(updateDto.description());
        
        // 거래 조건
        if (updateDto.paymentTerm() != null) customer.setPaymentTerm(updateDto.paymentTerm());
        if (updateDto.customPaymentDays() != null) customer.setCustomPaymentDays(updateDto.customPaymentDays());
        if (updateDto.creditLimit() != null) customer.setCreditLimit(updateDto.creditLimit());
        if (updateDto.discountRate() != null) customer.setDiscountRate(updateDto.discountRate());
        if (updateDto.taxRate() != null) customer.setTaxRate(updateDto.taxRate());
        
        // 추가 정보
        if (updateDto.tags() != null) customer.setTags(updateDto.tags());
        if (updateDto.sortOrder() != null) customer.setSortOrder(updateDto.sortOrder());
        if (updateDto.metadata() != null) customer.setMetadata(updateDto.metadata());
    }

    private CustomerDto.CustomerResponseDto mapToResponseDto(Customer customer) {
        return new CustomerDto.CustomerResponseDto(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getCustomerName(),
                customer.getCustomerNameEn(),
                customer.getCompany().getId(),
                customer.getCompany() != null ? customer.getCompany().getName() : null,
                customer.getCustomerType(),
                customer.getCustomerTypeDescription(),
                customer.getCustomerStatus(),
                customer.getCustomerStatusDescription(),
                customer.getCustomerGrade(),
                customer.getCustomerGradeDescription(),
                customer.getIsActive(),
                customer.getBusinessRegistrationNumber(),
                customer.getRepresentativeName(),
                customer.getBusinessType(),
                customer.getBusinessItem(),
                customer.getPhoneNumber(),
                customer.getFaxNumber(),
                customer.getEmail(),
                customer.getWebsite(),
                customer.getPostalCode(),
                customer.getAddress(),
                customer.getAddressDetail(),
                customer.getCity(),
                customer.getDistrict(),
                customer.getCountry(),
                customer.getFullAddress(),
                customer.getSalesManagerId(),
                customer.getSalesManagerName(),
                customer.getFirstContactDate(),
                customer.getLastContactDate(),
                customer.getDescription(),
                customer.getPaymentTerm(),
                customer.getPaymentTermDescription(),
                customer.getCustomPaymentDays(),
                customer.getCreditLimit(),
                customer.getDiscountRate(),
                customer.getTaxRate(),
                customer.getTotalOrderCount(),
                customer.getTotalOrderAmount(),
                customer.getLastOrderDate(),
                customer.getAverageOrderAmount(),
                customer.getOutstandingAmount(),
                customer.getTags(),
                customer.getSortOrder(),
                customer.getMetadata(),
                customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null,
                customer.getUpdatedAt() != null ? customer.getUpdatedAt().toString() : null,
                customer.isVipCustomer(),
                customer.isActiveCustomer(),
                customer.isCreditLimitExceeded(),
                customer.getCustomerSummary()
        );
    }

    private CustomerDto.CustomerSummaryDto mapToSummaryDto(Customer customer) {
        return new CustomerDto.CustomerSummaryDto(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getCustomerName(),
                customer.getCustomerType(),
                customer.getCustomerTypeDescription(),
                customer.getCustomerStatus(),
                customer.getCustomerStatusDescription(),
                customer.getCustomerGrade(),
                customer.getCustomerGradeDescription(),
                customer.getIsActive(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getSalesManagerName(),
                customer.getTotalOrderCount(),
                customer.getTotalOrderAmount(),
                customer.getLastOrderDate(),
                customer.getOutstandingAmount(),
                customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null,
                customer.isVipCustomer(),
                customer.isCreditLimitExceeded()
        );
    }

    private Customer.CustomerGrade determineGradeByOrderAmount(BigDecimal totalAmount) {
        if (totalAmount.compareTo(new BigDecimal("100000000")) >= 0) { // 1억 이상
            return Customer.CustomerGrade.PLATINUM;
        } else if (totalAmount.compareTo(new BigDecimal("50000000")) >= 0) { // 5천만 이상
            return Customer.CustomerGrade.GOLD;
        } else if (totalAmount.compareTo(new BigDecimal("10000000")) >= 0) { // 1천만 이상
            return Customer.CustomerGrade.SILVER;
        } else if (totalAmount.compareTo(new BigDecimal("1000000")) >= 0) { // 100만 이상
            return Customer.CustomerGrade.BRONZE;
        } else {
            return Customer.CustomerGrade.GENERAL;
        }
    }

    private Double calculateAverageOrdersPerCustomer(Long companyId) {
        List<Customer> customers = customerRepository.findByCompanyIdAndIsActiveTrueAndIsDeletedFalse(companyId);
        if (customers.isEmpty()) return 0.0;
        
        double totalOrders = customers.stream()
                .mapToInt(customer -> customer.getTotalOrderCount() != null ? customer.getTotalOrderCount() : 0)
                .sum();
        
        return totalOrders / customers.size();
    }
}
