package com.erp.hr.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;

/**
 * 근태 엔티티
 * 직원의 출퇴근 및 근무 시간을 관리합니다
 */
@Entity
@Table(name = "attendances", indexes = {
    @Index(name = "idx_attendances_employee", columnList = "employee_id"),
    @Index(name = "idx_attendances_date", columnList = "attendance_date"),
    @Index(name = "idx_attendances_status", columnList = "attendance_status"),
    @Index(name = "idx_attendances_employee_date", columnList = "employee_id, attendance_date")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_attendances_employee_date", columnNames = {"employee_id", "attendance_date"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    /**
     * 직원
     */
    @NotNull(message = "직원 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * 근무일
     */
    @NotNull(message = "근무일은 필수입니다")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * 출근 시간
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    /**
     * 퇴근 시간
     */
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    /**
     * 휴게 시작 시간
     */
    @Column(name = "break_start_time")
    private LocalDateTime breakStartTime;

    /**
     * 휴게 종료 시간
     */
    @Column(name = "break_end_time")
    private LocalDateTime breakEndTime;

    /**
     * 실제 근무 시간 (분 단위)
     */
    @Min(value = 0, message = "근무 시간은 0 이상이어야 합니다")
    @Column(name = "work_minutes")
    private Integer workMinutes;

    /**
     * 초과 근무 시간 (분 단위)
     */
    @Min(value = 0, message = "초과 근무 시간은 0 이상이어야 합니다")
    @Column(name = "overtime_minutes")
    private Integer overtimeMinutes = 0;

    /**
     * 야간 근무 시간 (분 단위)
     */
    @Min(value = 0, message = "야간 근무 시간은 0 이상이어야 합니다")
    @Column(name = "night_work_minutes")
    private Integer nightWorkMinutes = 0;

    /**
     * 휴일 근무 시간 (분 단위)
     */
    @Min(value = 0, message = "휴일 근무 시간은 0 이상이어야 합니다")
    @Column(name = "holiday_work_minutes")
    private Integer holidayWorkMinutes = 0;

    /**
     * 지각 시간 (분 단위)
     */
    @Min(value = 0, message = "지각 시간은 0 이상이어야 합니다")
    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    /**
     * 조퇴 시간 (분 단위)
     */
    @Min(value = 0, message = "조퇴 시간은 0 이상이어야 합니다")
    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    /**
     * 근태 상태
     */
    @NotNull(message = "근태 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 20)
    private AttendanceStatus attendanceStatus;

    /**
     * 근무 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 20)
    private WorkType workType = WorkType.OFFICE;

    /**
     * 출근 위치 (GPS 좌표)
     */
    @Size(max = 100, message = "출근 위치는 100자 이하여야 합니다")
    @Column(name = "check_in_location", length = 100)
    private String checkInLocation;

    /**
     * 퇴근 위치 (GPS 좌표)
     */
    @Size(max = 100, message = "퇴근 위치는 100자 이하여야 합니다")
    @Column(name = "check_out_location", length = 100)
    private String checkOutLocation;

    /**
     * 출근 IP 주소
     */
    @Size(max = 45, message = "IP 주소는 45자 이하여야 합니다")
    @Column(name = "check_in_ip", length = 45)
    private String checkInIp;

    /**
     * 퇴근 IP 주소
     */
    @Size(max = 45, message = "IP 주소는 45자 이하여야 합니다")
    @Column(name = "check_out_ip", length = 45)
    private String checkOutIp;

    /**
     * 비고
     */
    @Size(max = 500, message = "비고는 500자 이하여야 합니다")
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * 승인자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    /**
     * 승인 시간
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 승인 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /**
     * 근태 상태 열거형
     */
    public enum AttendanceStatus {
        PRESENT("출근"),
        ABSENT("결근"),
        LATE("지각"),
        EARLY_LEAVE("조퇴"),
        HALF_DAY("반차"),
        FULL_DAY_OFF("연차"),
        SICK_LEAVE("병가"),
        PERSONAL_LEAVE("개인사유"),
        BUSINESS_TRIP("출장"),
        EDUCATION("교육"),
        HOLIDAY("휴일");

        private final String description;

        AttendanceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 근무 유형 열거형
     */
    public enum WorkType {
        OFFICE("사무실 근무"),
        REMOTE("재택근무"),
        HYBRID("하이브리드"),
        FIELD("현장근무"),
        BUSINESS_TRIP("출장");

        private final String description;

        WorkType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 승인 상태 열거형
     */
    public enum ApprovalStatus {
        PENDING("승인대기"),
        APPROVED("승인"),
        REJECTED("반려");

        private final String description;

        ApprovalStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 출근 처리
     */
    public void checkIn(LocalDateTime checkInTime, String location, String ipAddress) {
        this.checkInTime = checkInTime;
        this.checkInLocation = location;
        this.checkInIp = ipAddress;
        
        // 지각 여부 확인 (오전 9시 기준)
        LocalTime standardStartTime = LocalTime.of(9, 0);
        if (checkInTime.toLocalTime().isAfter(standardStartTime)) {
            this.lateMinutes = (int) Duration.between(
                checkInTime.toLocalDate().atTime(standardStartTime),
                checkInTime
            ).toMinutes();
            this.attendanceStatus = AttendanceStatus.LATE;
        } else {
            this.attendanceStatus = AttendanceStatus.PRESENT;
        }
    }

    /**
     * 퇴근 처리
     */
    public void checkOut(LocalDateTime checkOutTime, String location, String ipAddress) {
        this.checkOutTime = checkOutTime;
        this.checkOutLocation = location;
        this.checkOutIp = ipAddress;
        
        // 근무 시간 계산
        if (this.checkInTime != null) {
            calculateWorkTime();
        }
    }

    /**
     * 근무 시간 계산
     */
    public void calculateWorkTime() {
        if (checkInTime == null || checkOutTime == null) {
            return;
        }
        
        // 전체 근무 시간 계산
        long totalMinutes = Duration.between(checkInTime, checkOutTime).toMinutes();
        
        // 휴게 시간 제외
        long breakMinutes = 0;
        if (breakStartTime != null && breakEndTime != null) {
            breakMinutes = Duration.between(breakStartTime, breakEndTime).toMinutes();
        } else {
            // 기본 점심시간 1시간 제외
            breakMinutes = 60;
        }
        
        this.workMinutes = (int) Math.max(0, totalMinutes - breakMinutes);
        
        // 초과 근무 시간 계산 (8시간 초과)
        int standardWorkMinutes = 8 * 60; // 8시간
        if (this.workMinutes > standardWorkMinutes) {
            this.overtimeMinutes = this.workMinutes - standardWorkMinutes;
        }
        
        // 야간 근무 시간 계산 (22시 ~ 06시)
        calculateNightWorkTime();
        
        // 조퇴 여부 확인 (오후 6시 기준)
        LocalTime standardEndTime = LocalTime.of(18, 0);
        if (checkOutTime.toLocalTime().isBefore(standardEndTime)) {
            this.earlyLeaveMinutes = (int) Duration.between(
                checkOutTime,
                checkOutTime.toLocalDate().atTime(standardEndTime)
            ).toMinutes();
            
            if (this.attendanceStatus == AttendanceStatus.PRESENT) {
                this.attendanceStatus = AttendanceStatus.EARLY_LEAVE;
            }
        }
    }

    /**
     * 야간 근무 시간 계산
     */
    private void calculateNightWorkTime() {
        if (checkInTime == null || checkOutTime == null) {
            return;
        }
        
        LocalTime nightStart = LocalTime.of(22, 0); // 22시
        LocalTime nightEnd = LocalTime.of(6, 0);    // 06시
        
        int nightMinutes = 0;
        LocalDateTime current = checkInTime;
        
        while (current.isBefore(checkOutTime)) {
            LocalTime currentTime = current.toLocalTime();
            
            if (currentTime.isAfter(nightStart) || currentTime.isBefore(nightEnd)) {
                nightMinutes++;
            }
            
            current = current.plusMinutes(1);
        }
        
        this.nightWorkMinutes = nightMinutes;
    }

    /**
     * 휴게 시작
     */
    public void startBreak(LocalDateTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    /**
     * 휴게 종료
     */
    public void endBreak(LocalDateTime breakEndTime) {
        this.breakEndTime = breakEndTime;
        // 근무 시간 재계산
        if (checkInTime != null && checkOutTime != null) {
            calculateWorkTime();
        }
    }

    /**
     * 근태 승인
     */
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    /**
     * 근태 반려
     */
    public void reject(Employee approver, String reason) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.remarks = (this.remarks != null ? this.remarks + "\n" : "") + 
                      "반려 사유: " + reason;
    }

    /**
     * 근무 시간을 시간:분 형식으로 반환
     */
    public String getWorkTimeFormatted() {
        if (workMinutes == null) {
            return "00:00";
        }
        int hours = workMinutes / 60;
        int minutes = workMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * 초과 근무 시간을 시간:분 형식으로 반환
     */
    public String getOvertimeFormatted() {
        if (overtimeMinutes == null) {
            return "00:00";
        }
        int hours = overtimeMinutes / 60;
        int minutes = overtimeMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * 정상 근무인지 확인
     */
    public boolean isNormalAttendance() {
        return attendanceStatus == AttendanceStatus.PRESENT && 
               lateMinutes == 0 && earlyLeaveMinutes == 0;
    }
}




