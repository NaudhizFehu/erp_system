package com.erp.common.security;

import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 직원 세부 정보 서비스 구현체
 * Spring Security에서 직원 인증 시 직원 정보를 로드합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    /**
     * 사용자명으로 직원 정보 로드
     * Spring Security에서 인증 시 자동으로 호출됩니다
     *
     * @param username 사용자명 또는 이메일
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 직원을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("직원 정보 로드 시도: {}", username);

        // 사용자명 또는 이메일로 직원 검색 (회사 및 부서 정보 포함)
        Employee employee = employeeRepository.findByUsername(username)
                .or(() -> employeeRepository.findByEmail(username))
                .orElseThrow(() -> {
                    log.warn("직원을 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("직원을 찾을 수 없습니다: " + username);
                });

        // 직원 계정 상태 검증
        validateEmployeeAccount(employee, username);

        log.debug("직원 정보 로드 완료: {} (ID: {}, Role: {})",
            employee.getUsername(), employee.getId(), employee.getRole());

        return UserPrincipal.create(employee);
    }

    /**
     * 직원 ID로 직원 정보 로드
     * JWT 토큰 검증 시 사용됩니다
     *
     * @param employeeId 직원 ID
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 직원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long employeeId) throws UsernameNotFoundException {
        log.debug("직원 ID로 정보 로드 시도: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("직원 ID를 찾을 수 없습니다: {}", employeeId);
                    return new UsernameNotFoundException("직원 ID를 찾을 수 없습니다: " + employeeId);
                });

        // 직원 계정 상태 검증
        validateEmployeeAccount(employee, employeeId.toString());

        log.debug("직원 ID로 정보 로드 완료: {} (Username: {}, Role: {})",
            employeeId, employee.getUsername(), employee.getRole());

        return UserPrincipal.create(employee);
    }

    /**
     * 활성 직원만 로드 (비활성화되거나 삭제된 직원 제외)
     *
     * @param username 사용자명 또는 이메일
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 활성 직원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserDetails loadActiveUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("활성 직원 정보 로드 시도: {}", username);

        Employee employee = employeeRepository.findByUsername(username)
                .or(() -> employeeRepository.findByEmail(username))
                .filter(e -> e.getIsActive() != null && e.getIsActive())
                .orElseThrow(() -> {
                    log.warn("활성 직원을 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("활성 직원을 찾을 수 없습니다: " + username);
                });

        // 추가 계정 상태 검증
        validateEmployeeAccount(employee, username);

        log.debug("활성 직원 정보 로드 완료: {} (ID: {}, Role: {})",
            employee.getUsername(), employee.getId(), employee.getRole());

        return UserPrincipal.create(employee);
    }

    /**
     * 직원 계정 상태 검증
     * 계정 잠금, 비활성화, 삭제 등의 상태를 확인합니다
     *
     * @param employee 검증할 직원
     * @param identifier 직원 식별자 (로깅용)
     * @throws UsernameNotFoundException 계정 상태가 유효하지 않은 경우
     */
    private void validateEmployeeAccount(Employee employee, String identifier) {
        // username이 없는 직원은 로그인 불가 (시스템 접근 권한 없음)
        if (employee.getUsername() == null || employee.getUsername().trim().isEmpty()) {
            log.warn("시스템 접근 권한이 없는 직원: {}", identifier);
            throw new UsernameNotFoundException("시스템 접근 권한이 없습니다: " + identifier);
        }

        if (employee.getIsActive() != null && !employee.getIsActive()) {
            log.warn("비활성화된 직원 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("비활성화된 직원입니다: " + identifier);
        }

        if (employee.getIsLocked() != null && employee.getIsLocked()) {
            log.warn("잠긴 직원 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("잠긴 계정입니다: " + identifier);
        }

        // 퇴사자 확인
        if (employee.getEmploymentStatus() == Employee.EmploymentStatus.TERMINATED) {
            log.warn("퇴사자 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("퇴사한 직원입니다: " + identifier);
        }

        // 회사 상태 검증
        if (employee.getCompany() != null && !employee.getCompany().isActive()) {
            log.warn("비활성화된 회사 소속 직원 접근 시도: {} (Company: {})",
                identifier, employee.getCompany().getName());
            throw new UsernameNotFoundException("소속 회사가 비활성화되었습니다: " + identifier);
        }

        // 부서 상태 검증
        if (employee.getDepartment() != null && !employee.getDepartment().isActive()) {
            log.warn("비활성화된 부서 소속 직원 접근 시도: {} (Department: {})",
                identifier, employee.getDepartment().getName());
            throw new UsernameNotFoundException("소속 부서가 비활성화되었습니다: " + identifier);
        }
    }

    /**
     * 사용자명 존재 여부 확인
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return employeeRepository.findByUsername(username).isPresent();
    }

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

    /**
     * 직원 마지막 로그인 시간 업데이트
     *
     * @param employeeId 직원 ID
     */
    @Transactional
    public void updateLastLoginTime(Long employeeId) {
        try {
            employeeRepository.findById(employeeId).ifPresent(employee -> {
                employee.setLastLoginAt(java.time.LocalDateTime.now());
                employeeRepository.save(employee);
            });
            log.debug("직원 마지막 로그인 시간 업데이트: {}", employeeId);
        } catch (Exception e) {
            log.error("마지막 로그인 시간 업데이트 실패: {}", employeeId, e);
        }
    }

    /**
     * 직원 권한 새로고침 (캐시된 권한 정보 갱신)
     *
     * @param employeeId 직원 ID
     * @return 갱신된 UserDetails
     */
    @Transactional(readOnly = true)
    public UserDetails refreshUserAuthorities(Long employeeId) {
        log.debug("직원 권한 정보 새로고침: {}", employeeId);
        return loadUserById(employeeId);
    }

    /**
     * 특정 역할을 가진 직원 수 조회
     *
     * @param role 직원 역할
     * @return 해당 역할을 가진 직원 수
     */
    @Transactional(readOnly = true)
    public long countEmployeesByRole(Employee.UserRole role) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getRole() == role)
                .count();
    }

    /**
     * 특정 회사의 활성 직원 수 조회
     *
     * @param companyId 회사 ID
     * @return 해당 회사의 활성 직원 수
     */
    @Transactional(readOnly = true)
    public long countActiveEmployeesByCompany(Long companyId) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getCompany() != null && e.getCompany().getId().equals(companyId))
                .filter(e -> e.getIsActive() != null && e.getIsActive())
                .count();
    }
}
