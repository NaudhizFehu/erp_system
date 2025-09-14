package com.erp.common.dto;

import com.erp.common.entity.Department;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 부서 정보 응답 DTO
 * 부서 정보 조회 시 사용됩니다
 */
public record DepartmentDto(
        Long id,
        String departmentCode,
        String name,
        String nameEn,
        String description,
        CompanyDto company,
        DepartmentDto parentDepartment,
        List<DepartmentDto> subDepartments,
        UserDto manager,
        Integer level,
        Integer sortOrder,
        Department.DepartmentType departmentType,
        Department.DepartmentStatus status,
        String costCenterCode,
        String phone,
        String fax,
        String email,
        String location,
        BigDecimal budgetAmount,
        String fullPath,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public DepartmentDto {
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("부서 코드는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("부서명은 필수입니다");
        }
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (status == null) {
            throw new IllegalArgumentException("부서 상태는 필수입니다");
        }
        if (level == null || level < 1) {
            throw new IllegalArgumentException("부서 레벨은 1 이상이어야 합니다");
        }
    }

    /**
     * Department 엔티티를 DepartmentDto로 변환
     */
    public static DepartmentDto from(com.erp.common.entity.Department department) {
        if (department == null) return null;
        
        return new DepartmentDto(
            department.getId(),
            department.getDepartmentCode(),
            department.getName(),
            department.getNameEn(),
            department.getDescription(),
            CompanyDto.from(department.getCompany()),
            department.getParentDepartment() != null ? from(department.getParentDepartment()) : null,
            null, // subDepartments - 순환 참조 방지를 위해 null로 설정
            null, // manager - 순환 참조 방지를 위해 null로 설정
            department.getLevel(),
            department.getSortOrder(),
            null, // departmentType - 엔티티에 없는 필드
            null, // status - 엔티티에 없는 필드  
            null, // costCenterCode - 엔티티에 없는 필드
            null, // phone - 엔티티에 없는 필드
            null, // fax - 엔티티에 없는 필드
            null, // email - 엔티티에 없는 필드
            null, // location - 엔티티에 없는 필드
            null, // budgetAmount - 엔티티에 없는 필드
            null, // fullPath - 엔티티에 없는 필드
            department.getCreatedAt(),
            department.getUpdatedAt()
        );
    }
}
