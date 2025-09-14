package com.erp.inventory.service.impl;

import com.erp.inventory.dto.WarehouseDto;
import com.erp.inventory.repository.WarehouseRepository;
import com.erp.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

/**
 * 창고 서비스 구현체 (기본 구현)
 * TODO: 실제 비즈니스 로직 구현 필요
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public WarehouseDto.WarehouseResponseDto createWarehouse(WarehouseDto.WarehouseCreateDto createDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    @Transactional
    public WarehouseDto.WarehouseResponseDto updateWarehouse(Long id, WarehouseDto.WarehouseUpdateDto updateDto) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public WarehouseDto.WarehouseResponseDto getWarehouse(Long id) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public WarehouseDto.WarehouseResponseDto getWarehouseByCode(String warehouseCode) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public Page<WarehouseDto.WarehouseResponseDto> getAllWarehouses(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public Page<WarehouseDto.WarehouseResponseDto> getWarehousesByCompany(Long companyId, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public List<WarehouseDto.WarehouseResponseDto> getActiveWarehouses() {
        return new ArrayList<>();
    }

    @Override
    public Page<WarehouseDto.WarehouseResponseDto> searchWarehouses(String searchTerm, Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        throw new UnsupportedOperationException("구현 예정");
    }

    @Override
    public boolean isWarehouseCodeExists(String warehouseCode) {
        return false;
    }

    @Override
    public List<Object[]> getInventoryCountByWarehouse() {
        return new ArrayList<>();
    }

    @Override
    public List<Object[]> getWarehouseCountByType() {
        return new ArrayList<>();
    }
}