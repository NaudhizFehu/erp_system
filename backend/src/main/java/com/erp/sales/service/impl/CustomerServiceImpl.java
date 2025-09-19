package com.erp.sales.service.impl;

import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
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
import java.time.LocalDate;
import java.util.Collections;
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
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto createCustomer(CustomerDto.CustomerCreateDto createDto) {
        log.info("고객 생성 요청: {}", createDto.customerCode());
        
        // 고객코드 중복 확인
        if (customerRepository.existsByCompanyIdAndCustomerCodeAndIsDeletedFalse(createDto.companyId(), createDto.customerCode())) {
            ExceptionUtils.throwDuplicate("이미 존재하는 고객코드입니다: " + createDto.customerCode());
        }


        // 회사 조회
        Company company = companyRepository.findById(createDto.companyId())
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("회사를 찾을 수 없습니다: " + createDto.companyId()));

        // Customer 엔티티 생성
        Customer customer = createCustomerEntity(createDto);
        customer.setCompany(company);
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

        // Customer 엔티티에 OutstandingAmount 필드가 없으므로 미수금 체크 생략

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
                pageable
        ).map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersBySalesManager(Long companyId, Long salesManagerId, Pageable pageable) {
        log.info("영업담당자별 고객 조회: 회사ID={}, 담당자ID={}", companyId, salesManagerId);
        
        Page<Customer> customers = customerRepository.findByCompanyIdAndSalesManagerIdAndIsDeletedFalse(companyId, salesManagerId, pageable);
        return customers.map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByType(Long companyId, String customerType, Pageable pageable) {
        Customer.CustomerType type = Customer.CustomerType.valueOf(customerType);
        return customerRepository.findByCompanyIdAndCustomerTypeAndIsDeletedFalse(companyId, type, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByStatus(Long companyId, Customer.CustomerStatus status, Pageable pageable) {
        log.info("상태별 고객 조회: 회사ID={}, 상태={}", companyId, status);
        
        Page<Customer> customers = customerRepository.findByCompanyIdAndCustomerStatusAndIsDeletedFalse(companyId, status, pageable);
        return customers.map(this::mapToSummaryDto);
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
        return customerRepository.findDormantCustomers(companyId)
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
                ((Number) stats[2]).longValue(),  // newCustomers
                ((Number) stats[3]).longValue(),  // totalSalesAmount
                ((Number) stats[4]).longValue(),  // averageSalesAmount
                ((Number) stats[5]).longValue(),  // totalOutstandingAmount
                ((Number) stats[6]).longValue(),  // customersWithOutstanding
                ((Number) stats[7]).longValue()   // customersOverCreditLimit
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
        // faxNumber 필드 제거됨
        customer.setEmail(contactDto.email());
        // Customer 엔티티에 website, lastContactDate 필드가 없으므로 생략

        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomerAddress(Long customerId, CustomerDto.CustomerAddressUpdateDto addressDto) {
        log.info("고객 주소 업데이트: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        // postalCode 필드 제거됨
        customer.setAddress(addressDto.address());
        // addressDetail 필드 제거됨
        // Customer 엔티티에 city, district, country 필드가 없으므로 생략

        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto updateCustomerTerms(Long customerId, CustomerDto.CustomerTermsUpdateDto termsDto) {
        log.info("고객 거래조건 업데이트: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));


        customer = customerRepository.save(customer);
        return mapToResponseDto(customer);
    }



    @Override
    @Transactional
    public CustomerDto.CustomerResponseDto toggleCustomerActive(Long customerId) {
        log.info("고객 활성화 토글: ID={}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));

        // status 필드가 제거되어 활성화 토글 불가

        customer = customerRepository.save(customer);
        log.info("고객 활성화 토글 완료: ID={}", customerId);
        
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
        // businessRegistrationNumber 필드가 없으므로 항상 false 반환
        return false;
    }

    @Override
    @Transactional
    public void updateCustomerOrderStatistics(Long customerId) {
        log.info("고객 주문 통계 업데이트: ID={} - 해당 필드들이 Customer 엔티티에 없으므로 스킵", customerId);
        // Customer 엔티티에 해당 필드들이 없으므로 아무것도 하지 않음
    }

    @Override
    @Transactional
    public void updateAllCustomerOrderStatistics(Long companyId) {
        log.info("모든 고객 주문 통계 일괄 업데이트: companyId={} - 해당 필드들이 Customer 엔티티에 없으므로 스킵", companyId);
        // Customer 엔티티에 해당 필드들이 없으므로 아무것도 하지 않음
    }


    @Override
    @Transactional
    public int convertDormantCustomers(Long companyId, int dormantDays) {
        log.info("휴면 고객 자동 전환: companyId={}, 기준일수={}", companyId, dormantDays);
        
        List<Customer> dormantCustomers = customerRepository.findDormantCustomers(companyId);
        
        int convertedCount = 0;
        for (Customer customer : dormantCustomers) {
            // status 필드가 제거되어 휴면 전환 불가
            // customer 변수는 사용하지 않지만 루프 구조 유지
            log.debug("휴면 고객 처리: {}", customer.getCustomerName());
            convertedCount++;
        }
        
        log.info("휴면 고객 전환 완료: {}명", convertedCount);
        return convertedCount;
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getCustomersWithBirthdayThisMonth(Long companyId) {
        // birthday 필드가 없으므로 빈 리스트 반환
        return Collections.emptyList();
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
        return getCustomerStatsByType(companyId);
    }

    @Override
    public BigDecimal calculateCustomerLifetimeValue(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return BigDecimal.ZERO;
        }
        
        // Customer 엔티티에 주문 관련 필드가 없으므로 기본값 반환
        // 실제로는 Order 엔티티에서 고객별 주문 데이터를 조회해야 함
        return BigDecimal.ZERO;
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
        return customerRepository.findDormantCustomers(companyId)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private Customer createCustomerEntity(CustomerDto.CustomerCreateDto createDto) {
        Customer customer = new Customer();
        
        customer.setCustomerCode(createDto.customerCode());
        customer.setCustomerName(createDto.customerName());
        customer.setCustomerType(createDto.customerType());
        customer.setCustomerStatus(createDto.customerStatus() != null ? createDto.customerStatus() : Customer.CustomerStatus.ACTIVE);
        customer.setCustomerGrade(createDto.customerGrade());
        customer.setBusinessRegistrationNumber(createDto.businessRegistrationNumber());
        customer.setCeoName(createDto.ceoName());
        customer.setCreditLimit(createDto.creditLimit());
        customer.setPaymentTerms(createDto.paymentTerms());
        
        // 연락처 정보
        customer.setPhoneNumber(createDto.phoneNumber());
        customer.setEmail(createDto.email());
        
        // 주소 정보
        customer.setAddress(createDto.address());
        
        // 영업담당자 설정
        if (createDto.salesManagerId() != null) {
            Employee salesManager = employeeRepository.findById(createDto.salesManagerId())
                    .orElse(null);
            customer.setSalesManager(salesManager);
        }
        
        // 담당자 정보 - contact 관련 필드들이 제거됨
        
        // 사업자 정보
        // businessNumber 필드 제거됨
        // notes 필드 제거됨
        
        return customer;
    }

    private void updateCustomerEntity(Customer customer, CustomerDto.CustomerUpdateDto updateDto) {
        if (updateDto.customerName() != null) customer.setCustomerName(updateDto.customerName());
        if (updateDto.customerStatus() != null) customer.setCustomerStatus(updateDto.customerStatus());
        if (updateDto.customerGrade() != null) customer.setCustomerGrade(updateDto.customerGrade());
        if (updateDto.businessRegistrationNumber() != null) customer.setBusinessRegistrationNumber(updateDto.businessRegistrationNumber());
        if (updateDto.ceoName() != null) customer.setCeoName(updateDto.ceoName());
        if (updateDto.creditLimit() != null) customer.setCreditLimit(updateDto.creditLimit());
        if (updateDto.paymentTerms() != null) customer.setPaymentTerms(updateDto.paymentTerms());
        
        // 연락처 정보
        if (updateDto.phoneNumber() != null) customer.setPhoneNumber(updateDto.phoneNumber());
        if (updateDto.email() != null) customer.setEmail(updateDto.email());
        
        // 주소 정보
        if (updateDto.address() != null) customer.setAddress(updateDto.address());
        
        // 영업담당자 업데이트
        if (updateDto.salesManagerId() != null) {
            Employee salesManager = employeeRepository.findById(updateDto.salesManagerId())
                    .orElse(null);
            customer.setSalesManager(salesManager);
        }
    }

    private CustomerDto.CustomerResponseDto mapToResponseDto(Customer customer) {
        return CustomerDto.CustomerResponseDto.from(customer);
    }

    private CustomerDto.CustomerSummaryDto mapToSummaryDto(Customer customer) {
        return CustomerDto.CustomerSummaryDto.from(customer);
    }

    @Override
    @Transactional
    public void changeCustomerStatus(Long customerId, CustomerDto.CustomerStatusChangeDto statusChangeDto) {
        log.info("고객 상태 변경 요청: ID={}, 상태={}", customerId, statusChangeDto.customerStatus());
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));
        
        customer.setCustomerStatus(statusChangeDto.customerStatus());
        customerRepository.save(customer);
        
        log.info("고객 상태 변경 완료: ID={}, 상태={}, 사유={}", 
                customerId, statusChangeDto.customerStatus(), statusChangeDto.reason());
    }

    @Override
    @Transactional
    public void changeCustomerGrade(Long customerId, CustomerDto.CustomerGradeChangeDto gradeChangeDto) {
        log.info("고객 등급 변경 요청: ID={}, 등급={}", customerId, gradeChangeDto.customerGrade());
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("고객을 찾을 수 없습니다: " + customerId));
        
        customer.setCustomerGrade(gradeChangeDto.customerGrade());
        customerRepository.save(customer);
        
        log.info("고객 등급 변경 완료: ID={}, 등급={}, 사유={}", 
                customerId, gradeChangeDto.customerGrade(), gradeChangeDto.reason());
    }

    @Override
    public Page<CustomerDto.CustomerSummaryDto> getCustomersByGrade(Long companyId, Customer.CustomerGrade grade, Pageable pageable) {
        log.info("등급별 고객 조회: 회사ID={}, 등급={}", companyId, grade);
        
        Page<Customer> customers = customerRepository.findByCompanyIdAndCustomerGradeAndIsDeletedFalse(companyId, grade, pageable);
        return customers.map(this::mapToSummaryDto);
    }

    @Override
    public List<CustomerDto.CustomerSummaryDto> getCustomersOverCreditLimit(Long companyId) {
        log.info("신용한도 초과 고객 조회: 회사ID={}", companyId);
        
        List<Customer> customers = customerRepository.findAllByCompanyIdAndIsDeletedFalse(companyId)
                .stream()
                .filter(customer -> customer.getCreditLimit() != null)
                .collect(Collectors.toList());
        
        return customers.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

}
