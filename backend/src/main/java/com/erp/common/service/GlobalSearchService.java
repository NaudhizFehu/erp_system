package com.erp.common.service;

import com.erp.common.dto.GlobalSearchDto;
import com.erp.common.entity.Company;
import com.erp.common.entity.Department;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.repository.DepartmentRepository;
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
            results.addAll(searchCompanies(query));
            results.addAll(searchCustomers(query, companyId));
            results.addAll(searchEmployees(query, companyId));
            results.addAll(searchProducts(query, companyId));
            results.addAll(searchOrders(query, companyId));
            results.addAll(searchDepartments(query, companyId));
            
            log.info("전역 검색 완료: 총 {}개 결과", results.size());
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
     * 고객 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchCustomers(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("고객 검색 시작: query={}, companyId={}", query, companyId);
            List<Customer> customers = customerRepository.findByCompanyIdAndCustomerNameContainingIgnoreCase(companyId, query);
            log.info("검색된 고객 수: {}", customers.size());
            
            for (Customer customer : customers) {
                log.info("고객 정보: id={}, name={}, type={}, grade={}", 
                    customer.getId(), customer.getCustomerName(), 
                    customer.getCustomerType(), customer.getCustomerGrade());
                
                results.add(new GlobalSearchDto.SearchResult(
                    customer.getId().toString(),
                    customer.getCustomerName(),
                    String.format("%s - %s등급", 
                        customer.getCustomerType() == Customer.CustomerType.CORPORATE ? "법인" : "개인",
                        customer.getCustomerGrade() != null ? customer.getCustomerGrade() : "일반"),
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
     * 직원 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchEmployees(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            log.info("직원 검색 시작: query={}, companyId={}", query, companyId);
            List<Employee> employees = employeeRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, query);
            log.info("검색된 직원 수: {}", employees.size());
            
            for (Employee employee : employees) {
                log.info("직원 정보: id={}, name={}, department={}, position={}", 
                    employee.getId(), employee.getName(), 
                    employee.getDepartment() != null ? employee.getDepartment().getName() : "null",
                    employee.getPosition() != null ? employee.getPosition().getName() : "null");
                
                results.add(new GlobalSearchDto.SearchResult(
                    employee.getId().toString(),
                    employee.getName(),
                    String.format("%s - %s", 
                        employee.getDepartment() != null ? employee.getDepartment().getName() : "부서미지정",
                        employee.getPosition() != null ? employee.getPosition().getName() : "직급미지정"),
                    "employee",
                    "/hr/employees/" + employee.getId()
                ));
            }
        } catch (Exception e) {
            log.error("직원 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 상품 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchProducts(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            List<Product> products = productRepository.findByCompanyIdAndProductNameContainingIgnoreCase(companyId, query);
            
            for (Product product : products) {
                results.add(new GlobalSearchDto.SearchResult(
                    product.getId().toString(),
                    product.getProductName(),
                    String.format("상품 - %s", 
                        product.getCategory() != null ? product.getCategory().getName() : "카테고리미지정"),
                    "product",
                    "/inventory/products/" + product.getId()
                ));
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
            List<Order> orders = orderRepository.findByCompanyIdAndOrderNumberContainingIgnoreCase(companyId, query);
            
            for (Order order : orders) {
                results.add(new GlobalSearchDto.SearchResult(
                    order.getId().toString(),
                    order.getOrderNumber(),
                    String.format("고객: %s - %s", 
                        order.getCustomer() != null ? order.getCustomer().getCustomerName() : "고객미지정",
                        order.getOrderDate().toString()),
                    "order",
                    "/sales/orders/" + order.getId()
                ));
            }
        } catch (Exception e) {
            log.error("주문 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }

    /**
     * 부서 검색
     */
    @Transactional(readOnly = true)
    public List<GlobalSearchDto.SearchResult> searchDepartments(String query, Long companyId) {
        List<GlobalSearchDto.SearchResult> results = new ArrayList<>();
        
        try {
            List<Department> departments = departmentRepository.findByCompanyIdAndDepartmentNameContainingIgnoreCase(companyId, query);
            
            for (Department department : departments) {
                results.add(new GlobalSearchDto.SearchResult(
                    department.getId().toString(),
                    department.getName(),
                    String.format("부서 - %s", 
                        department.getCompany() != null ? department.getCompany().getName() : "회사미지정"),
                    "department",
                    "/hr/departments/" + department.getId()
                ));
            }
        } catch (Exception e) {
            log.error("부서 검색 중 오류 발생: {}", e.getMessage(), e);
        }
        
        return results;
    }
}
