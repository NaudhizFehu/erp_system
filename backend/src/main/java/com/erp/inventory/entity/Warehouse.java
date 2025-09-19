package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 창고 엔티티
 * 재고를 보관하는 창고 정보를 관리합니다
 */
@Entity
@Table(
    name = "warehouses",
    indexes = {
        @Index(name = "idx_warehouse_company", columnList = "company_id"),
        @Index(name = "idx_warehouse_code", columnList = "company_id, warehouse_code"),
        @Index(name = "idx_warehouse_type", columnList = "company_id, warehouse_type"),
        @Index(name = "idx_warehouse_active", columnList = "company_id, is_active")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_code", columnNames = {"company_id", "warehouse_code"})
    }
)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"inventories", "stockMovements"})
@ToString(exclude = {"inventories", "stockMovements"})
public class Warehouse extends BaseEntity {

    /**
     * 창고 유형 열거형
     */
    public enum WarehouseType {
        MAIN("본창고"),
        SUB("보조창고"),
        EXTERNAL("외부창고"),
        VIRTUAL("가상창고"),
        CONSIGNMENT("위탁창고"),
        QUARANTINE("격리창고"),
        RETURNED("반품창고"),
        DAMAGED("불량창고");

        private final String description;

        WarehouseType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 창고 상태 열거형
     */
    public enum WarehouseStatus {
        ACTIVE("운영중"),
        INACTIVE("중단"),
        MAINTENANCE("점검중"),
        CLOSED("폐쇄");

        private final String description;

        WarehouseStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 창고 코드 (회사별 고유)
     */
    @NotBlank(message = "창고 코드는 필수입니다")
    @Size(max = 20, message = "창고 코드는 20자를 초과할 수 없습니다")
    @Column(name = "warehouse_code", nullable = false, length = 20)
    private String warehouseCode;

    /**
     * 창고명
     */
    @NotBlank(message = "창고명은 필수입니다")
    @Size(max = 100, message = "창고명은 100자를 초과할 수 없습니다")
    @Column(name = "warehouse_name", nullable = false, length = 100)
    private String warehouseName;

    /**
     * 창고명 (영문)
     */
    @Size(max = 100, message = "영문 창고명은 100자를 초과할 수 없습니다")
    @Column(name = "warehouse_name_en", length = 100)
    private String warehouseNameEn;

    /**
     * 설명
     */
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 회사 정보
     */
    @NotNull(message = "회사 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_warehouse_company"))
    private Company company;

    /**
     * 창고 유형
     */
    @NotNull(message = "창고 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", nullable = false, length = 20)
    private WarehouseType warehouseType;

    /**
     * 창고 상태
     */
    @NotNull(message = "창고 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_status", nullable = false, length = 20)
    private WarehouseStatus warehouseStatus = WarehouseStatus.ACTIVE;

    /**
     * 활성 상태
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 주소
     */
    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
    @Column(name = "address", length = 200)
    private String address;


    /**
     * 우편번호
     */
    @Size(max = 10, message = "우편번호는 10자를 초과할 수 없습니다")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * 전화번호
     */
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * 팩스번호
     */
    @Size(max = 20, message = "팩스번호는 20자를 초과할 수 없습니다")
    @Column(name = "fax_number", length = 20)
    private String faxNumber;

    /**
     * 이메일
     */
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 담당자명
     */
    @Size(max = 50, message = "담당자명은 50자를 초과할 수 없습니다")
    @Column(name = "manager_name", length = 50)
    private String managerName;

    /**
     * 담당자 연락처
     */
    @Size(max = 20, message = "담당자 연락처는 20자를 초과할 수 없습니다")
    @Column(name = "manager_phone", length = 20)
    private String managerPhone;

    /**
     * 총 면적 (평방미터)
     */
    @Column(name = "total_area")
    private Double totalArea;

    /**
     * 사용 가능 면적 (평방미터)
     */
    @Column(name = "usable_area")
    private Double usableArea;

    /**
     * 최대 수용량
     */
    @Column(name = "max_capacity")
    private Double maxCapacity;

    /**
     * 현재 사용량
     */
    @Column(name = "current_usage")
    private Double currentUsage = 0.0;

    /**
     * 온도 조건 (섭씨)
     */
    @Column(name = "temperature_min")
    private Double temperatureMin;

    @Column(name = "temperature_max")
    private Double temperatureMax;

    /**
     * 습도 조건 (%)
     */
    @Column(name = "humidity_min")
    private Double humidityMin;

    @Column(name = "humidity_max")
    private Double humidityMax;

    /**
     * 특수 조건
     */
    @Size(max = 500, message = "특수 조건은 500자를 초과할 수 없습니다")
    @Column(name = "special_conditions", length = 500)
    private String specialConditions;

    /**
     * 보안 등급
     */
    @Size(max = 20, message = "보안 등급은 20자를 초과할 수 없습니다")
    @Column(name = "security_level", length = 20)
    private String securityLevel;

    /**
     * 접근 권한 정보
     */
    @Size(max = 500, message = "접근 권한 정보는 500자를 초과할 수 없습니다")
    @Column(name = "access_permissions", length = 500)
    private String accessPermissions;

    /**
     * 운영 시간 정보
     */
    @Size(max = 200, message = "운영 시간 정보는 200자를 초과할 수 없습니다")
    @Column(name = "operating_hours", length = 200)
    private String operatingHours;

    /**
     * GPS 위도
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * GPS 경도
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 메타데이터 (JSON 형태)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * 이 창고의 재고 목록
     */
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();

    /**
     * 이 창고의 재고이동 내역
     */
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<StockMovement> stockMovements = new ArrayList<>();

    // 편의 메서드들

    /**
     * 사용률 계산 (%)
     */
    public Double getUsageRate() {
        if (maxCapacity == null || maxCapacity == 0) {
            return 0.0;
        }
        return (currentUsage / maxCapacity) * 100;
    }

    /**
     * 남은 수용량
     */
    public Double getRemainingCapacity() {
        if (maxCapacity == null) {
            return null;
        }
        return maxCapacity - (currentUsage != null ? currentUsage : 0.0);
    }

    /**
     * 수용량 부족 여부
     */
    public boolean isCapacityExceeded() {
        if (maxCapacity == null) {
            return false;
        }
        return currentUsage != null && currentUsage > maxCapacity;
    }

    /**
     * 온도 범위 내 여부 확인
     */
    public boolean isTemperatureInRange(Double temperature) {
        if (temperature == null) {
            return true;
        }
        
        boolean minOk = temperatureMin == null || temperature >= temperatureMin;
        boolean maxOk = temperatureMax == null || temperature <= temperatureMax;
        
        return minOk && maxOk;
    }

    /**
     * 습도 범위 내 여부 확인
     */
    public boolean isHumidityInRange(Double humidity) {
        if (humidity == null) {
            return true;
        }
        
        boolean minOk = humidityMin == null || humidity >= humidityMin;
        boolean maxOk = humidityMax == null || humidity <= humidityMax;
        
        return minOk && maxOk;
    }

    /**
     * 창고 운영 가능 여부
     */
    public boolean isOperational() {
        return isActive && warehouseStatus == WarehouseStatus.ACTIVE;
    }

    /**
     * 총 재고 품목 수
     */
    public long getTotalProductCount() {
        return inventories.stream()
                .filter(inv -> inv.getQuantity() != null && inv.getQuantity() > 0)
                .count();
    }

    /**
     * 총 재고 수량
     */
    public Double getTotalStockQuantity() {
        return inventories.stream()
                .filter(inv -> inv.getQuantity() != null)
                .mapToDouble(Inventory::getQuantity)
                .sum();
    }

    /**
     * 안전재고 미달 품목 수
     */
    public long getLowStockCount() {
        // minStock 필드가 제거되어 단순화
        return 0;
    }

    /**
     * 재고없음 품목 수
     */
    public long getOutOfStockCount() {
        return inventories.stream()
                .filter(inventory -> inventory.getQuantity() == 0)
                .count();
    }

    /**
     * 과재고 품목 수
     */
    public long getOverStockCount() {
        return inventories.stream()
                .filter(inventory -> inventory.getQuantity() > inventory.getMaxStock())
                .count();
    }

    /**
     * 창고 상태 설명 조회
     */
    public String getStatusDescription() {
        if (!isActive) {
            return "비활성";
        }
        return warehouseStatus.getDescription();
    }

    /**
     * 전체 주소 조회
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (address != null) {
            fullAddress.append(address);
        }
        
        
        if (postalCode != null) {
            if (fullAddress.length() > 0) {
                fullAddress.append(" (").append(postalCode).append(")");
            } else {
                fullAddress.append(postalCode);
            }
        }
        
        return fullAddress.toString();
    }

    /**
     * 사용량 업데이트
     */
    public void updateCurrentUsage() {
        this.currentUsage = getTotalStockQuantity();
    }

    @PreUpdate
    private void preUpdate() {
        updateCurrentUsage();
    }
}
