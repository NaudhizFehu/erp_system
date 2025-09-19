package com.erp.common.service;

import com.erp.common.dto.GlobalSearchDto;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.hr.entity.Department;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.inventory.entity.Product;
import com.erp.inventory.repository.ProductRepository;
import com.erp.sales.entity.Customer;
import com.erp.sales.entity.Order;
import com.erp.sales.repository.CustomerRepository;
import com.erp.sales.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 전역 검색 서비스
 * 모든 모듈에서 통합 검색을 제공합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;

    /**
     * 모든 모듈에서 통합 검색
     */
    public List<GlobalSearchDto.SearchResult> searchAll(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        log.info("전역 검색 시작: query={}, companyId={}", query, companyId);
        
        try {
            // 각 모듈별 검색 결과를 추가
            List<GlobalSearchDto.SearchResult> companyResults = searchCompanies(query);
            log.info("회사 검색 결과: {}개", companyResults.size());
            results.addAll(companyResults);
            
            List<GlobalSearchDto.SearchResult> customerResults = searchCustomers(query, companyId);
            log.info("고객 검색 결과: {}개", customerResults.size());
            results.addAll(customerResults);
            
            List<GlobalSearchDto.SearchResult> employeeResults = searchEmployees(query, companyId);
            log.info("직원 검색 결과: {}개", employeeResults.size());
            results.addAll(employeeResults);
            
            List<GlobalSearchDto.SearchResult> productResults = searchProducts(query, companyId);
            log.info("상품 검색 결과: {}개", productResults.size());
            results.addAll(productResults);
            
            List<GlobalSearchDto.SearchResult> orderResults = searchOrders(query, companyId);
            log.info("주문 검색 결과: {}개", orderResults.size());
            results.addAll(orderResults);
            
            List<GlobalSearchDto.SearchResult> departmentResults = searchDepartments(query, companyId);
            log.info("부서 검색 결과: {}개", departmentResults.size());
            results.addAll(departmentResults);
            
            log.info("전역 검색 완료: 총 {}개 결과", results.size());
            
            // 각 결과의 상세 정보 로깅
            for (int i = 0; i < results.size(); i++) {
                GlobalSearchDto.SearchResult result = results.get(i);
                log.info("결과 {}: id={}, title={}, type={}, description={}", 
                    i + 1, result.id(), result.title(), result.type(), result.description());
            }
        } catch (Exception e) {
            log.error("전역 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 회사 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchCompanies(String query) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("회사 검색 시작: query={}", query);
            List<Company> companies = companyRepository.findByNameContaining(query);
            log.info("검색된 회사 수: {}", companies.size());
            
            for (Company company : companies) {
                log.info("회사 정보: id={}, name={}, businessType={}, address={}", 
                    company.getId(), company.getName(), company.getBusinessType(), company.getAddress());
                
                results.add(new GlobalSearchDto.SearchResult(
                    company.getId().toString(),
                    company.getName(),
                    String.format("회사 - %s | %s", 
                        company.getBusinessType() != null ? company.getBusinessType() : "업종미지정",
                        company.getAddress() != null ? company.getAddress() : "주소미지정"),
                    "company",
                    "/companies/" + company.getId()
                ));
            }
        } catch (Exception e) {
            log.error("회사 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 고객 검색 (개선된 검색 로직)
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchCustomers(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("고객 검색 시작: query={}, companyId={}", query, companyId);
            
            List<Customer> customers;
            
            // 검색어가 비어있거나 "고객"인 경우 모든 고객 조회
            if (query.trim().isEmpty() || "고객".equals(query.trim())) {
                customers = customerRepository.findByCompanyId(companyId);
                log.info("모든 고객 조회: {}명", customers.size());
            } else {
                // 고객명, 고객코드로 검색
                customers = customerRepository.findByCompanyIdAndCustomerNameContainingIgnoreCase(companyId, query);
                log.info("검색된 고객 수: {}", customers.size());
            }
            
            for (Customer customer : customers) {
                log.info("고객 정보: id={}, name={}, type={}", 
                    customer.getId(), customer.getCustomerName(), 
                    customer.getCustomerType());
                
                results.add(new GlobalSearchDto.SearchResult(
                    customer.getId().toString(),
                    customer.getCustomerName(),
                    String.format("%s", 
                        customer.getCustomerType() == Customer.CustomerType.CORPORATE ? "법인" : "개인"),
                    "customer",
                    "/sales/customers/" + customer.getId()
                ));
            }
        } catch (Exception e) {
            log.error("고객 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 직원 검색 (개선된 검색 로직)
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchEmployees(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("직원 검색 시작: query={}, companyId={}", query, companyId);
            
            List<Employee> employees;
            
            // 검색어가 비어있거나 "직원"인 경우 모든 직원 조회
            if (query.trim().isEmpty() || "직원".equals(query.trim())) {
                employees = employeeRepository.findByCompanyId(companyId);
                log.info("모든 직원 조회: {}명", employees.size());
            } else {
                // 이름, 이메일, 직원번호로 검색
                employees = employeeRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, query);
                log.info("검색된 직원 수: {}", employees.size());
            }
            
            for (Employee employee : employees) {
                log.info("직원 정보: id={}, name={}, department={}, position={}", 
                    employee.getId(), employee.getName(), 
                    employee.getDepartment() != null ? employee.getDepartment().getName() : "null",
                    employee.getPosition() != null ? employee.getPosition().getName() : "null");
                
                String description = String.format("%s - %s", 
                    employee.getDepartment() != null ? employee.getDepartment().getName() : "부서미지정",
                    employee.getPosition() != null ? employee.getPosition().getName() : "직급미지정");
                
                GlobalSearchDto.SearchResult result = new GlobalSearchDto.SearchResult(
                    employee.getId().toString(),
                    employee.getName(),
                    description,
                    "employee",
                    "/hr/employees/" + employee.getId()
                );
                
                log.info("직원 검색 결과 생성: id={}, title={}, description={}", 
                    result.id(), result.title(), result.description());
                
                results.add(result);
            }
        } catch (Exception e) {
            log.error("직원 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 상품 검색 (개선된 검색 로직)
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchProducts(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("상품 검색 시작: query={}, companyId={}", query, companyId);
            
            List<Product> products;
            
            // 검색어가 비어있거나 "상품"인 경우 모든 상품 조회
            if (query.trim().isEmpty() || "상품".equals(query.trim())) {
                products = productRepository.findByCompanyId(companyId);
                log.info("모든 상품 조회: {}개", products.size());
            } else {
                // 상품명, 상품코드로 검색
                products = productRepository.findByCompanyIdAndProductNameContainingIgnoreCase(companyId, query);
                log.info("검색된 상품 수: {}", products.size());
            }
            
            for (Product product : products) {
                log.info("상품 정보: id={}, name={}, category={}", 
                    product.getId(), product.getProductName(),
                    product.getCategory() != null ? product.getCategory().getName() : "null");
                
                String description = String.format("상품 - %s", 
                    product.getCategory() != null ? product.getCategory().getName() : "카테고리미지정");
                
                GlobalSearchDto.SearchResult result = new GlobalSearchDto.SearchResult(
                    product.getId().toString(),
                    product.getProductName(),
                    description,
                    "product",
                    "/inventory/products/" + product.getId()
                );
                
                log.info("상품 검색 결과 생성: id={}, title={}, description={}", 
                    result.id(), result.title(), result.description());
                
                results.add(result);
            }
        } catch (Exception e) {
            log.error("상품 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 주문 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchOrders(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("주문 검색 시작: query={}, companyId={}", query, companyId);
            List<Order> orders = orderRepository.findByCompanyIdAndOrderNumberContainingIgnoreCaseAndIsDeletedFalse(companyId, query);
            log.info("검색된 주문 수: {}", orders.size());
            
            for (Order order : orders) {
                log.info("주문 정보: id={}, orderNumber={}, customer={}, orderDate={}", 
                    order.getId(), order.getOrderNumber(),
                    order.getCustomer() != null ? order.getCustomer().getCustomerName() : "null",
                    order.getOrderDate());
                
                String description = String.format("고객: %s - %s", 
                    order.getCustomer() != null ? order.getCustomer().getCustomerName() : "고객미지정",
                    order.getOrderDate().toString());
                
                GlobalSearchDto.SearchResult result = new GlobalSearchDto.SearchResult(
                    order.getId().toString(),
                    order.getOrderNumber(),
                    description,
                    "order",
                    "/sales/orders/" + order.getId()
                );
                
                log.info("주문 검색 결과 생성: id={}, title={}, description={}", 
                    result.id(), result.title(), result.description());
                
                results.add(result);
            }
        } catch (Exception e) {
            log.error("주문 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 부서 검색 (개선된 검색 로직)
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchDepartments(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("부서 검색 시작: query={}, companyId={}", query, companyId);
            
            List<Department> departments;
            
            // 검색어가 비어있거나 "부서"인 경우 모든 부서 조회
            if (query.trim().isEmpty() || "부서".equals(query.trim())) {
                departments = departmentRepository.findByCompanyId(companyId);
                log.info("모든 부서 조회: {}개", departments.size());
            } else {
                // 부서명으로 검색
                departments = departmentRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, query);
                log.info("검색된 부서 수: {}", departments.size());
            }
            
            for (Department department : departments) {
                log.info("부서 정보: id={}, name={}, company={}", 
                    department.getId(), department.getName(),
                    department.getCompany() != null ? department.getCompany().getName() : "null");
                
                String description = String.format("부서 - %s", 
                    department.getCompany() != null ? department.getCompany().getName() : "회사미지정");
                
                GlobalSearchDto.SearchResult result = new GlobalSearchDto.SearchResult(
                    department.getId().toString(),
                    department.getName(),
                    description,
                    "department",
                    "/hr/departments/" + department.getId()
                );
                
                log.info("부서 검색 결과 생성: id={}, title={}, description={}", 
                    result.id(), result.title(), result.description());
                
                results.add(result);
            }
        } catch (Exception e) {
            log.error("부서 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }
}
