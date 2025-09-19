package com.erp.hr.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.hr.entity.Position;
import java.time.LocalDateTime;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직급 정보 응답 DTO
 * 직급 정보 조회 시 사용됩니다
 */
public record PositionDto(
        Long id,
        String positionCode,
        String name,
        String description,
        CompanyDto company,
        Integer level,
        Boolean isActive,
        String authorities,
        Integer employeeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public PositionDto {
        if (positionCode == null || positionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("직급 코드는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("직급명은 필수입니다");
        }
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (isActive == null) {
            throw new IllegalArgumentException("사용 여부는 필수입니다");
        }
    }
    
    /**
     * Position 엔티티로부터 PositionDto 생성
     */
    public static PositionDto from(Position position) {
        return new PositionDto(
            position.getId(),
            position.getPositionCode(),
            position.getName(),
            position.getDescription(),
            position.getCompany() != null ? CompanyDto.from(position.getCompany()) : null,
            position.getLevel(),
            position.getIsActive(),
            null, // authorities 필드 제거됨
            null, // employeeCount 필드는 Position 엔티티에 없으므로 null로 설정
            position.getCreatedAt(),
            position.getUpdatedAt()
        );
    }
}




