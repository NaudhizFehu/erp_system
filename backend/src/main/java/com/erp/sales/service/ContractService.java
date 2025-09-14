package com.erp.sales.service;

import com.erp.sales.dto.ContractDto;
import com.erp.sales.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * 계약 서비스 인터페이스 (기본 구현)
 * 계약 관련 기본적인 비즈니스 로직을 정의합니다
 */
public interface ContractService {

    /**
     * 계약 생성
     */
    ContractDto.ContractResponseDto createContract(ContractDto.ContractCreateDto createDto);

    /**
     * 계약 수정
     */
    ContractDto.ContractResponseDto updateContract(Long contractId, ContractDto.ContractUpdateDto updateDto);

    /**
     * 계약 삭제
     */
    void deleteContract(Long contractId);

    /**
     * 계약 조회
     */
    ContractDto.ContractResponseDto getContract(Long contractId);

    /**
     * 계약 번호로 조회
     */
    ContractDto.ContractResponseDto getContractByNumber(String contractNumber);

    /**
     * 전체 계약 목록 조회
     */
    Page<ContractDto.ContractResponseDto> getAllContracts(Pageable pageable);

    /**
     * 고객별 계약 목록 조회
     */
    Page<ContractDto.ContractResponseDto> getContractsByCustomer(Long customerId, Pageable pageable);

    /**
     * 상태별 계약 목록 조회
     */
    Page<ContractDto.ContractResponseDto> getContractsByStatus(Contract.ContractStatus status, Pageable pageable);

    /**
     * 계약 검색
     */
    Page<ContractDto.ContractResponseDto> searchContracts(ContractDto.ContractSearchDto searchDto, Pageable pageable);

    /**
     * 만료 예정 계약 조회
     */
    List<ContractDto.ContractResponseDto> getExpiringContracts(int days);

    /**
     * 갱신 예정 계약 조회
     */
    List<ContractDto.ContractResponseDto> getRenewalDueContracts(int days);

    /**
     * 계약 상태 변경
     */
    ContractDto.ContractResponseDto changeContractStatus(Long contractId, ContractDto.ContractStatusChangeDto statusChangeDto);

    /**
     * 계약 갱신
     */
    ContractDto.ContractResponseDto renewContract(Long contractId, ContractDto.ContractRenewalDto renewalDto);

    /**
     * 계약 해지
     */
    ContractDto.ContractResponseDto terminateContract(Long contractId, String terminationReason);

    /**
     * 계약 통계
     */
    ContractDto.ContractStatsDto getContractStatistics(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 상태별 계약 수 통계
     */
    List<Object[]> getContractCountByStatus();

    /**
     * 계약 유형별 통계
     */
    List<Object[]> getContractCountByType();

    /**
     * 고객별 계약 수 통계
     */
    List<Object[]> getContractCountByCustomer();
}