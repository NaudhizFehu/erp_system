package com.erp.hr.service;

import com.erp.hr.dto.PositionCreateDto;
import com.erp.hr.dto.PositionDto;
import com.erp.hr.dto.PositionUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 직급 서비스 인터페이스 (기본 구현)
 * 직급 관련 기본적인 비즈니스 로직을 정의합니다
 */
public interface PositionService {

    /**
     * 직급 생성
     */
    PositionDto createPosition(PositionCreateDto createDto);

    /**
     * 직급 정보 수정
     */
    PositionDto updatePosition(Long id, PositionUpdateDto updateDto);

    /**
     * 직급 조회 (ID)
     */
    PositionDto getPosition(Long id);

    /**
     * 직급 코드로 조회
     */
    PositionDto getPositionByCode(String positionCode);

    /**
     * 전체 직급 목록 조회 (페이징)
     */
    Page<PositionDto> getAllPositions(Pageable pageable);

    /**
     * 회사별 직급 목록 조회 (페이징)
     */
    Page<PositionDto> getPositionsByCompany(Long companyId, Pageable pageable);

    /**
     * 활성 직급 목록 조회
     */
    List<PositionDto> getActivePositions();

    /**
     * 레벨별 직급 조회
     */
    List<PositionDto> getPositionsByLevel(Integer level);

    /**
     * 직급 검색
     */
    Page<PositionDto> searchPositions(String searchTerm, Pageable pageable);

    /**
     * 직급 삭제 (소프트 삭제)
     */
    void deletePosition(Long id);

    /**
     * 직급 코드 중복 확인
     */
    boolean isPositionCodeExists(String positionCode);

    /**
     * 직급 코드 중복 확인 (본인 제외)
     */
    boolean isPositionCodeExists(String positionCode, Long excludeId);

    /**
     * 직급별 직원 수 통계
     */
    List<Object[]> getEmployeeCountByPosition();

    /**
     * 레벨별 직급 수 통계
     */
    List<Object[]> getPositionCountByLevel();

    /**
     * 직급별 평균 급여 통계
     */
    List<Object[]> getAverageSalaryByPosition();

    // ==================== PositionController에서 사용하는 추가 메서드들 ====================
    
    /**
     * 회사별 활성 직급 목록 조회
     */
    List<PositionDto> getActivePositionsByCompany(Long companyId);
    
    // getPositionsByCategory 메서드 제거됨 (positionCategory 필드가 DB 스키마에 없음)
    
    /**
     * 유형별 직급 조회
     */
    // getPositionsByType 메서드 제거됨 (positionType 필드가 DB 스키마에 없음)
    
    /**
     * 회사별 직급 검색
     */
    Page<PositionDto> searchPositionsByCompany(Long companyId, String searchTerm, Pageable pageable);
    
    /**
     * 승진 가능한 직급 조회
     */
    List<PositionDto> getPromotablePositions(Long currentPositionId, Integer targetLevel);
    
    /**
     * 급여 범위별 직급 조회
     */
    List<PositionDto> getPositionsBySalaryRange(java.math.BigDecimal minSalary);
    
    /**
     * 직급 상태 토글
     */
    PositionDto togglePositionStatus(Long id);
    
    /**
     * 회사 내 직급 코드 중복 확인 (수정 시)
     */
    boolean isPositionCodeExistsInCompany(Long companyId, String positionCode, Long excludeId);
    
    /**
     * 회사 내 직급 코드 중복 확인 (생성 시)
     */
    boolean isPositionCodeExistsInCompany(Long companyId, String positionCode);
    
    /**
     * 카테고리별 직급 수 통계
     */
    List<Object[]> getPositionCountByCategory();
    
    /**
     * 유형별 직급 수 통계
     */
    List<Object[]> getPositionCountByType();
    
    /**
     * 회사별 직급 수 통계
     */
    List<Object[]> getPositionCountByCompany();
}