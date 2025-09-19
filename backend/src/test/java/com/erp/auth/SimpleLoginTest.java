package com.erp.auth;

import com.erp.common.dto.auth.LoginRequest;
import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import com.erp.common.security.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 간단한 로그인 테스트
 * 데이터베이스에 저장된 계정 정보를 직접 확인합니다
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleLoginTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("admin 계정 존재 확인")
    void testAdminUserExists() {
        // Given & When
        var adminUser = userRepository.findByUsernameWithCompanyAndDepartment("admin");

        // Then
        assertThat(adminUser).isPresent();
        User user = adminUser.get();
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getRole()).isEqualTo(User.UserRole.ADMIN);
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getIsLocked()).isFalse();
        assertThat(user.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("user 계정 존재 확인")
    void testUserExists() {
        // Given & When
        var normalUser = userRepository.findByUsernameWithCompanyAndDepartment("user");

        // Then
        assertThat(normalUser).isPresent();
        User user = normalUser.get();
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(user.getRole()).isEqualTo(User.UserRole.USER);
        assertThat(user.getIsActive()).isTrue();
        assertThat(user.getIsLocked()).isFalse();
        assertThat(user.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("admin 계정 비밀번호 검증")
    void testAdminPassword() {
        // Given
        var adminUser = userRepository.findByUsernameWithCompanyAndDepartment("admin");
        assertThat(adminUser).isPresent();

        // When
        User user = adminUser.get();
        boolean adminPasswordMatches = passwordEncoder.matches("admin123", user.getPassword());
        boolean userPasswordMatches = passwordEncoder.matches("user123", user.getPassword());

        // Then
        assertThat(adminPasswordMatches).isTrue();
        assertThat(userPasswordMatches).isFalse();
    }

    @Test
    @DisplayName("user 계정 비밀번호 검증")
    void testUserPassword() {
        // Given
        var normalUser = userRepository.findByUsernameWithCompanyAndDepartment("user");
        assertThat(normalUser).isPresent();

        // When
        User user = normalUser.get();
        boolean adminPasswordMatches = passwordEncoder.matches("admin123", user.getPassword());
        boolean userPasswordMatches = passwordEncoder.matches("user123", user.getPassword());

        // Then
        assertThat(adminPasswordMatches).isFalse();
        assertThat(userPasswordMatches).isTrue();
    }

    @Test
    @DisplayName("CustomUserDetailsService로 admin 계정 로드")
    void testLoadAdminUser() {
        // Given & When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("CustomUserDetailsService로 user 계정 로드")
    void testLoadUser() {
        // Given & When
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("user");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("로그인 요청 DTO 생성 테스트")
    void testLoginRequest() {
        // Given & When
        LoginRequest adminLogin = new LoginRequest("admin", "admin123", false);
        LoginRequest userLogin = new LoginRequest("user", "user123", false);

        // Then
        assertThat(adminLogin.usernameOrEmail()).isEqualTo("admin");
        assertThat(adminLogin.password()).isEqualTo("admin123");
        assertThat(adminLogin.rememberMe()).isFalse();

        assertThat(userLogin.usernameOrEmail()).isEqualTo("user");
        assertThat(userLogin.password()).isEqualTo("user123");
        assertThat(userLogin.rememberMe()).isFalse();
    }
}
