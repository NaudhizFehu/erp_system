package com.erp.auth;

import com.erp.common.dto.auth.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 로그인 통합 테스트
 * admin/admin123과 user/user123 계정으로 로그인이 정상적으로 작동하는지 테스트합니다
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("admin 계정 정보 확인 테스트")
    void testAdminInfo() throws Exception {
        mockMvc.perform(get("/api/auth/debug/admin-info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.isLocked").value(false))
                .andExpect(jsonPath("$.data.isDeleted").value(false))
                .andExpect(jsonPath("$.data.adminPasswordMatches").value(true))
                .andExpect(jsonPath("$.data.userPasswordMatches").value(false));
    }

    @Test
    @DisplayName("admin 계정 로그인 테스트")
    void testAdminLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/debug/test-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.passwordMatches").value(true))
                .andExpect(jsonPath("$.data.authenticationSuccess").value(true))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.isLocked").value(false))
                .andExpect(jsonPath("$.data.isDeleted").value(false))
                .andReturn();

        System.out.println("=== Admin 로그인 테스트 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("admin 계정 실제 로그인 API 테스트")
    void testAdminRealLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.userInfo.username").value("admin"))
                .andExpect(jsonPath("$.data.userInfo.role").value("ADMIN"))
                .andReturn();

        System.out.println("=== Admin 실제 로그인 API 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("user 계정 로그인 테스트")
    void testUserLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "user123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/debug/test-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.passwordMatches").value(true))
                .andExpect(jsonPath("$.data.authenticationSuccess").value(true))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.isLocked").value(false))
                .andExpect(jsonPath("$.data.isDeleted").value(false))
                .andReturn();

        System.out.println("=== User 로그인 테스트 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("user 계정 실제 로그인 API 테스트")
    void testUserRealLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "user123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.userInfo.username").value("user"))
                .andExpect(jsonPath("$.data.userInfo.role").value("USER"))
                .andReturn();

        System.out.println("=== User 실제 로그인 API 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("잘못된 비밀번호 로그인 실패 테스트")
    void testWrongPasswordLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "wrongpassword", false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("로그인에 실패했습니다"));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 로그인 실패 테스트")
    void testNonExistentUserLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password", false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("로그인에 실패했습니다"));
    }

    @Test
    @DisplayName("admin 계정으로 user 비밀번호 시도 테스트")
    void testAdminWithUserPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "user123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/debug/test-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.passwordMatches").value(false))
                .andExpect(jsonPath("$.data.authenticationSuccess").value(false))
                .andReturn();

        System.out.println("=== Admin 계정으로 user 비밀번호 시도 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("user 계정으로 admin 비밀번호 시도 테스트")
    void testUserWithAdminPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "admin123", false);

        MvcResult result = mockMvc.perform(post("/api/auth/debug/test-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.passwordMatches").value(false))
                .andExpect(jsonPath("$.data.authenticationSuccess").value(false))
                .andReturn();

        System.out.println("=== User 계정으로 admin 비밀번호 시도 결과 ===");
        System.out.println(result.getResponse().getContentAsString());
    }
}
