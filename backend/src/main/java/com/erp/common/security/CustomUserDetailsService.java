package com.erp.common.security;

import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 세부 정보 서비스 구현체
 * Spring Security에서 사용자 인증 시 사용자 정보를 로드합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 사용자 정보 로드
     * Spring Security에서 인증 시 자동으로 호출됩니다
     * 
     * @param username 사용자명 또는 이메일
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 정보 로드 시도: {}", username);
        
        // 사용자명 또는 이메일로 사용자 검색 (회사 및 부서 정보 포함)
        User user = userRepository.findByUsernameWithCompanyAndDepartment(username)
                .or(() -> userRepository.findByEmailWithCompanyAndDepartment(username))
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        // 사용자 계정 상태 검증
        validateUserAccount(user, username);
        
        log.debug("사용자 정보 로드 완료: {} (ID: {}, Role: {})", 
            user.getUsername(), user.getId(), user.getRole());
        
        return UserPrincipal.create(user);
    }

    /**
     * 사용자 ID로 사용자 정보 로드
     * JWT 토큰 검증 시 사용됩니다
     * 
     * @param userId 사용자 ID
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("사용자 ID로 정보 로드 시도: {}", userId);
        
        User user = userRepository.findByIdWithCompanyAndDepartment(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 ID를 찾을 수 없습니다: {}", userId);
                    return new UsernameNotFoundException("사용자 ID를 찾을 수 없습니다: " + userId);
                });

        // 사용자 계정 상태 검증
        validateUserAccount(user, userId.toString());
        
        log.debug("사용자 ID로 정보 로드 완료: {} (Username: {}, Role: {})", 
            userId, user.getUsername(), user.getRole());
        
        return UserPrincipal.create(user);
    }

    /**
     * 활성 사용자만 로드 (비활성화되거나 삭제된 사용자 제외)
     * 
     * @param username 사용자명 또는 이메일
     * @return UserDetails 구현체 (UserPrincipal)
     * @throws UsernameNotFoundException 활성 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserDetails loadActiveUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("활성 사용자 정보 로드 시도: {}", username);
        
        User user = userRepository.findActiveByUsername(username)
                .or(() -> userRepository.findActiveByEmail(username))
                .orElseThrow(() -> {
                    log.warn("활성 사용자를 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("활성 사용자를 찾을 수 없습니다: " + username);
                });

        // 추가 계정 상태 검증
        validateUserAccount(user, username);
        
        log.debug("활성 사용자 정보 로드 완료: {} (ID: {}, Role: {})", 
            user.getUsername(), user.getId(), user.getRole());
        
        return UserPrincipal.create(user);
    }

    /**
     * 사용자 계정 상태 검증
     * 계정 잠금, 비활성화, 삭제 등의 상태를 확인합니다
     * 
     * @param user 검증할 사용자
     * @param identifier 사용자 식별자 (로깅용)
     * @throws UsernameNotFoundException 계정 상태가 유효하지 않은 경우
     */
    private void validateUserAccount(User user, String identifier) {
        if (user.getIsDeleted()) {
            log.warn("삭제된 사용자 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("삭제된 사용자입니다: " + identifier);
        }

        if (!user.getIsActive()) {
            log.warn("비활성화된 사용자 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("비활성화된 사용자입니다: " + identifier);
        }

        if (user.getIsLocked()) {
            log.warn("잠긴 사용자 접근 시도: {}", identifier);
            throw new UsernameNotFoundException("잠긴 사용자입니다: " + identifier);
        }

        // 회사 상태 검증
        if (user.getCompany() != null && !user.getCompany().isActive()) {
            log.warn("비활성화된 회사 소속 사용자 접근 시도: {} (Company: {})", 
                identifier, user.getCompany().getName());
            throw new UsernameNotFoundException("소속 회사가 비활성화되었습니다: " + identifier);
        }

        // 부서 상태 검증
        if (user.getDepartment() != null && !user.getDepartment().isActive()) {
            log.warn("비활성화된 부서 소속 사용자 접근 시도: {} (Department: {})", 
                identifier, user.getDepartment().getName());
            throw new UsernameNotFoundException("소속 부서가 비활성화되었습니다: " + identifier);
        }
    }

    /**
     * 사용자 존재 여부 확인
     * 
     * @param username 사용자명
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 이메일 존재 여부 확인
     * 
     * @param email 이메일
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 활성 사용자 존재 여부 확인
     * 
     * @param username 사용자명
     * @return 활성 사용자 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsActiveUserByUsername(String username) {
        return userRepository.findActiveByUsername(username).isPresent();
    }

    /**
     * 활성 이메일 존재 여부 확인
     * 
     * @param email 이메일
     * @return 활성 이메일 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsActiveUserByEmail(String email) {
        return userRepository.findActiveByEmail(email).isPresent();
    }

    /**
     * 사용자 마지막 로그인 시간 업데이트
     * 
     * @param userId 사용자 ID
     */
    @Transactional
    public void updateLastLoginTime(Long userId) {
        try {
            userRepository.updateLastLoginAt(userId, java.time.LocalDateTime.now());
            log.debug("사용자 마지막 로그인 시간 업데이트: {}", userId);
        } catch (Exception e) {
            log.error("마지막 로그인 시간 업데이트 실패: {}", userId, e);
            // 로그인 시간 업데이트 실패가 인증에 영향을 주지 않도록 예외를 다시 던지지 않음
        }
    }

    /**
     * 사용자 권한 새로고침 (캐시된 권한 정보 갱신)
     * 
     * @param userId 사용자 ID
     * @return 갱신된 UserDetails
     */
    @Transactional(readOnly = true)
    public UserDetails refreshUserAuthorities(Long userId) {
        log.debug("사용자 권한 정보 새로고침: {}", userId);
        return loadUserById(userId);
    }

    /**
     * 특정 역할을 가진 사용자 수 조회
     * 
     * @param role 사용자 역할
     * @return 해당 역할을 가진 사용자 수
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).size();
    }

    /**
     * 특정 회사의 활성 사용자 수 조회
     * 
     * @param companyId 회사 ID
     * @return 해당 회사의 활성 사용자 수
     */
    @Transactional(readOnly = true)
    public long countActiveUsersByCompany(Long companyId) {
        return userRepository.findByCompanyId(companyId).stream()
                .filter(User::isEnabled)
                .count();
    }
}

