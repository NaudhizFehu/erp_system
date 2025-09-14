package com.erp.inventory.service;

import com.erp.inventory.dto.WarehouseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 창고 서비스 인터페이스
 * 창고 관련 비즈니스 로직을 정의합니다
 */
public interface WarehouseService {

    /**
     * 창고 생성
     */
    WarehouseDto.WarehouseResponseDto createWarehouse(WarehouseDto.WarehouseCreateDto createDto);

    /**
     * 창고 수정
     */
    WarehouseDto.WarehouseResponseDto updateWarehouse(Long id, WarehouseDto.WarehouseUpdateDto updateDto);

    /**
     * 창고 조회 (ID)
     */
    WarehouseDto.WarehouseResponseDto getWarehouse(Long id);

    /**
     * 창고 코드로 조회
     */
    WarehouseDto.WarehouseResponseDto getWarehouseByCode(String warehouseCode);

    /**
     * 전체 창고 목록 조회 (페이징)
     */
    Page<WarehouseDto.WarehouseResponseDto> getAllWarehouses(Pageable pageable);

    /**
     * 회사별 창고 목록 조회
     */
    Page<WarehouseDto.WarehouseResponseDto> getWarehousesByCompany(Long companyId, Pageable pageable);

    /**
     * 활성 창고 목록 조회
     */
    List<WarehouseDto.WarehouseResponseDto> getActiveWarehouses();

    /**
     * 창고 검색
     */
    Page<WarehouseDto.WarehouseResponseDto> searchWarehouses(String searchTerm, Pageable pageable);

    /**
     * 창고 삭제 (소프트 삭제)
     */
    void deleteWarehouse(Long id);

    /**
     * 창고 코드 중복 확인
     */
    boolean isWarehouseCodeExists(String warehouseCode);

    /**
     * 창고별 재고 통계
     */
    List<Object[]> getInventoryCountByWarehouse();

    /**
     * 창고 타입별 통계
     */
    List<Object[]> getWarehouseCountByType();
}




