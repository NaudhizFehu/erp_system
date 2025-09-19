package com.erp.inventory.dto;

import com.erp.inventory.entity.Warehouse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 창고 DTO 클래스들
 */
public class WarehouseDto {

    /**
     * 창고 생성 요청 DTO
     */
    public record WarehouseCreateDto(
            @NotBlank(message = "창고 코드는 필수입니다")
            @Size(max = 20, message = "창고 코드는 20자를 초과할 수 없습니다")
            String warehouseCode,

            @NotBlank(message = "창고명은 필수입니다")
            @Size(max = 100, message = "창고명은 100자를 초과할 수 없습니다")
            String warehouseName,

            @Size(max = 100, message = "영문 창고명은 100자를 초과할 수 없습니다")
            String warehouseNameEn,

            @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
            String description,

            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "창고 유형은 필수입니다")
            Warehouse.WarehouseType warehouseType,

            Warehouse.WarehouseStatus warehouseStatus,
            Boolean isActive,

            // 주소 정보
            @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
            String address,


            @Size(max = 10, message = "우편번호는 10자를 초과할 수 없습니다")
            String postalCode,

            // 연락처 정보
            @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
            String phoneNumber,

            @Size(max = 20, message = "팩스번호는 20자를 초과할 수 없습니다")
            String faxNumber,

            @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
            String email,

            // 담당자 정보
            @Size(max = 50, message = "담당자명은 50자를 초과할 수 없습니다")
            String managerName,

            @Size(max = 20, message = "담당자 연락처는 20자를 초과할 수 없습니다")
            String managerPhone,

            // 규격 정보
            @DecimalMin(value = "0.0", message = "총 면적은 0 이상이어야 합니다")
            Double totalArea,

            @DecimalMin(value = "0.0", message = "사용 가능 면적은 0 이상이어야 합니다")
            Double usableArea,

            @DecimalMin(value = "0.0", message = "최대 수용량은 0 이상이어야 합니다")
            Double maxCapacity,

            // 환경 조건
            Double temperatureMin,
            Double temperatureMax,
            Double humidityMin,
            Double humidityMax,

            @Size(max = 500, message = "특수 조건은 500자를 초과할 수 없습니다")
            String specialConditions,

            // 보안 및 접근
            @Size(max = 20, message = "보안 등급은 20자를 초과할 수 없습니다")
            String securityLevel,

            @Size(max = 500, message = "접근 권한 정보는 500자를 초과할 수 없습니다")
            String accessPermissions,

            @Size(max = 200, message = "운영 시간 정보는 200자를 초과할 수 없습니다")
            String operatingHours,

            // 위치 정보
            Double latitude,
            Double longitude,

            Integer sortOrder,
            String metadata
    ) {
        public WarehouseCreateDto {
            // 기본값 설정
            if (warehouseStatus == null) warehouseStatus = Warehouse.WarehouseStatus.ACTIVE;
            if (isActive == null) isActive = true;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 창고 수정 요청 DTO
     */
    public record WarehouseUpdateDto(
            @Size(max = 100, message = "창고명은 100자를 초과할 수 없습니다")
            String warehouseName,

            @Size(max = 100, message = "영문 창고명은 100자를 초과할 수 없습니다")
            String warehouseNameEn,

            @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
            String description,

            Warehouse.WarehouseType warehouseType,
            Warehouse.WarehouseStatus warehouseStatus,
            Boolean isActive,

            // 주소 정보
            @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
            String address,


            @Size(max = 10, message = "우편번호는 10자를 초과할 수 없습니다")
            String postalCode,

            // 연락처 정보
            @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
            String phoneNumber,

            @Size(max = 20, message = "팩스번호는 20자를 초과할 수 없습니다")
            String faxNumber,

            @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
            String email,

            // 담당자 정보
            @Size(max = 50, message = "담당자명은 50자를 초과할 수 없습니다")
            String managerName,

            @Size(max = 20, message = "담당자 연락처는 20자를 초과할 수 없습니다")
            String managerPhone,

            // 규격 정보
            @DecimalMin(value = "0.0", message = "총 면적은 0 이상이어야 합니다")
            Double totalArea,

            @DecimalMin(value = "0.0", message = "사용 가능 면적은 0 이상이어야 합니다")
            Double usableArea,

            @DecimalMin(value = "0.0", message = "최대 수용량은 0 이상이어야 합니다")
            Double maxCapacity,

            // 환경 조건
            Double temperatureMin,
            Double temperatureMax,
            Double humidityMin,
            Double humidityMax,

            @Size(max = 500, message = "특수 조건은 500자를 초과할 수 없습니다")
            String specialConditions,

            // 보안 및 접근
            @Size(max = 20, message = "보안 등급은 20자를 초과할 수 없습니다")
            String securityLevel,

            @Size(max = 500, message = "접근 권한 정보는 500자를 초과할 수 없습니다")
            String accessPermissions,

            @Size(max = 200, message = "운영 시간 정보는 200자를 초과할 수 없습니다")
            String operatingHours,

            // 위치 정보
            Double latitude,
            Double longitude,

            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 창고 응답 DTO
     */
    public record WarehouseResponseDto(
            Long id,
            String warehouseCode,
            String warehouseName,
            String warehouseNameEn,
            String description,
            Long companyId,
            String companyName,
            Warehouse.WarehouseType warehouseType,
            String warehouseTypeDescription,
            Warehouse.WarehouseStatus warehouseStatus,
            String warehouseStatusDescription,
            Boolean isActive,
            String address,
            String postalCode,
            String fullAddress,
            String phoneNumber,
            String faxNumber,
            String email,
            String managerName,
            String managerPhone,
            Double totalArea,
            Double usableArea,
            Double maxCapacity,
            Double currentUsage,
            Double usageRate,
            Double remainingCapacity,
            Double temperatureMin,
            Double temperatureMax,
            Double humidityMin,
            Double humidityMax,
            String specialConditions,
            String securityLevel,
            String accessPermissions,
            String operatingHours,
            Double latitude,
            Double longitude,
            Integer sortOrder,
            String metadata,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            
            // 통계 정보
            Long totalProductCount,
            Double totalStockQuantity,
            Long lowStockCount,
            Long outOfStockCount,
            Long overStockCount,
            Boolean isOperational,
            Boolean isCapacityExceeded,
            String statusDescription
    ) {
        /**
         * Entity에서 DTO로 변환
         */
        public static WarehouseResponseDto from(Warehouse warehouse) {
            return new WarehouseResponseDto(
                    warehouse.getId(),
                    warehouse.getWarehouseCode(),
                    warehouse.getWarehouseName(),
                    warehouse.getWarehouseNameEn(),
                    warehouse.getDescription(),
                    warehouse.getCompany().getId(),
                    warehouse.getCompany().getName(),
                    warehouse.getWarehouseType(),
                    warehouse.getWarehouseType().getDescription(),
                    warehouse.getWarehouseStatus(),
                    warehouse.getWarehouseStatus().getDescription(),
                    warehouse.getIsActive(),
                    warehouse.getAddress(),
                    warehouse.getPostalCode(),
                    warehouse.getFullAddress(),
                    warehouse.getPhoneNumber(),
                    warehouse.getFaxNumber(),
                    warehouse.getEmail(),
                    warehouse.getManagerName(),
                    warehouse.getManagerPhone(),
                    warehouse.getTotalArea(),
                    warehouse.getUsableArea(),
                    warehouse.getMaxCapacity(),
                    warehouse.getCurrentUsage(),
                    warehouse.getUsageRate(),
                    warehouse.getRemainingCapacity(),
                    warehouse.getTemperatureMin(),
                    warehouse.getTemperatureMax(),
                    warehouse.getHumidityMin(),
                    warehouse.getHumidityMax(),
                    warehouse.getSpecialConditions(),
                    warehouse.getSecurityLevel(),
                    warehouse.getAccessPermissions(),
                    warehouse.getOperatingHours(),
                    warehouse.getLatitude(),
                    warehouse.getLongitude(),
                    warehouse.getSortOrder(),
                    warehouse.getMetadata(),
                    warehouse.getCreatedAt(),
                    warehouse.getUpdatedAt(),
                    warehouse.getTotalProductCount(),
                    warehouse.getTotalStockQuantity(),
                    warehouse.getLowStockCount(),
                    warehouse.getOutOfStockCount(),
                    warehouse.getOverStockCount(),
                    warehouse.isOperational(),
                    warehouse.isCapacityExceeded(),
                    warehouse.getStatusDescription()
            );
        }
    }

    /**
     * 창고 요약 DTO (목록용)
     */
    public record WarehouseSummaryDto(
            Long id,
            String warehouseCode,
            String warehouseName,
            Warehouse.WarehouseType warehouseType,
            String warehouseTypeDescription,
            Warehouse.WarehouseStatus warehouseStatus,
            String warehouseStatusDescription,
            Boolean isActive,
            String fullAddress,
            String managerName,
            String managerPhone,
            Double maxCapacity,
            Double currentUsage,
            Double usageRate,
            Long totalProductCount,
            Long lowStockCount,
            Long outOfStockCount,
            Boolean isOperational,
            Boolean isCapacityExceeded
    ) {
        /**
         * Entity에서 요약 DTO로 변환
         */
        public static WarehouseSummaryDto from(Warehouse warehouse) {
            return new WarehouseSummaryDto(
                    warehouse.getId(),
                    warehouse.getWarehouseCode(),
                    warehouse.getWarehouseName(),
                    warehouse.getWarehouseType(),
                    warehouse.getWarehouseType().getDescription(),
                    warehouse.getWarehouseStatus(),
                    warehouse.getWarehouseStatus().getDescription(),
                    warehouse.getIsActive(),
                    warehouse.getFullAddress(),
                    warehouse.getManagerName(),
                    warehouse.getManagerPhone(),
                    warehouse.getMaxCapacity(),
                    warehouse.getCurrentUsage(),
                    warehouse.getUsageRate(),
                    warehouse.getTotalProductCount(),
                    warehouse.getLowStockCount(),
                    warehouse.getOutOfStockCount(),
                    warehouse.isOperational(),
                    warehouse.isCapacityExceeded()
            );
        }
    }

    /**
     * 창고 선택 DTO (선택 목록용)
     */
    public record WarehouseSelectDto(
            Long id,
            String warehouseCode,
            String warehouseName,
            Warehouse.WarehouseType warehouseType,
            Boolean isActive,
            Boolean isOperational
    ) {
        /**
         * Entity에서 선택 DTO로 변환
         */
        public static WarehouseSelectDto from(Warehouse warehouse) {
            return new WarehouseSelectDto(
                    warehouse.getId(),
                    warehouse.getWarehouseCode(),
                    warehouse.getWarehouseName(),
                    warehouse.getWarehouseType(),
                    warehouse.getIsActive(),
                    warehouse.isOperational()
            );
        }
    }

    /**
     * 창고 통계 DTO
     */
    public record WarehouseStatsDto(
            Long warehouseId,
            String warehouseName,
            Long totalInventoryItems,
            Double totalStockQuantity,
            Long activeProducts,
            Long inactiveProducts,
            Long lowStockItems,
            Long outOfStockItems,
            Long overStockItems,
            Long expiringSoonItems,
            Long expiredItems,
            Double utilizationRate,
            Double averageStockTurnover,
            LocalDateTime lastUpdated
    ) {}

    /**
     * 창고 용량 분석 DTO
     */
    public record WarehouseCapacityDto(
            Long warehouseId,
            String warehouseName,
            Double totalArea,
            Double usableArea,
            Double maxCapacity,
            Double currentUsage,
            Double usageRate,
            Double remainingCapacity,
            Boolean isCapacityExceeded,
            String capacityStatus,
            List<CapacityTrendDto> capacityTrend
    ) {
        /**
         * 용량 트렌드 DTO
         */
        public record CapacityTrendDto(
                LocalDateTime date,
                Double usage,
                Double usageRate
        ) {}
    }

    /**
     * 창고 환경 모니터링 DTO
     */
    public record WarehouseEnvironmentDto(
            Long warehouseId,
            String warehouseName,
            Double currentTemperature,
            Double temperatureMin,
            Double temperatureMax,
            Boolean isTemperatureInRange,
            Double currentHumidity,
            Double humidityMin,
            Double humidityMax,
            Boolean isHumidityInRange,
            String environmentStatus,
            LocalDateTime lastMonitored,
            List<EnvironmentLogDto> environmentLogs
    ) {
        /**
         * 환경 로그 DTO
         */
        public record EnvironmentLogDto(
                LocalDateTime timestamp,
                Double temperature,
                Double humidity,
                String status
        ) {}
    }

    /**
     * 창고 성과 분석 DTO
     */
    public record WarehousePerformanceDto(
            Long warehouseId,
            String warehouseName,
            LocalDateTime periodStart,
            LocalDateTime periodEnd,
            Long totalMovements,
            Long inboundMovements,
            Long outboundMovements,
            Double averageProcessingTime,
            Double accuracyRate,
            Long errorCount,
            Double productivityScore,
            List<PerformanceMetricDto> metrics
    ) {
        /**
         * 성과 지표 DTO
         */
        public record PerformanceMetricDto(
                String metricName,
                Double value,
                String unit,
                String trend
        ) {}
    }

    /**
     * 창고 검색 조건 DTO
     */
    public record WarehouseSearchDto(
            String searchTerm,
            Warehouse.WarehouseType warehouseType,
            Warehouse.WarehouseStatus warehouseStatus,
            Boolean isActive,
            Boolean isOperational,
            String city,
            String region,
            Double minCapacity,
            Double maxCapacity,
            Double minUsageRate,
            Double maxUsageRate,
            String sortBy,
            String sortDirection
    ) {}

    /**
     * 창고 위치 DTO (지도 표시용)
     */
    public record WarehouseLocationDto(
            Long id,
            String warehouseCode,
            String warehouseName,
            Warehouse.WarehouseType warehouseType,
            String address,
            Double latitude,
            Double longitude,
            Boolean isActive,
            Boolean isOperational,
            Long totalProducts,
            Double usageRate,
            String statusColor
    ) {
        /**
         * Entity에서 위치 DTO로 변환
         */
        public static WarehouseLocationDto from(Warehouse warehouse) {
            return new WarehouseLocationDto(
                    warehouse.getId(),
                    warehouse.getWarehouseCode(),
                    warehouse.getWarehouseName(),
                    warehouse.getWarehouseType(),
                    warehouse.getFullAddress(),
                    warehouse.getLatitude(),
                    warehouse.getLongitude(),
                    warehouse.getIsActive(),
                    warehouse.isOperational(),
                    warehouse.getTotalProductCount(),
                    warehouse.getUsageRate(),
                    warehouse.isOperational() ? "green" : "red"
            );
        }
    }
}
