package com.erp.hr.dto;

import com.erp.hr.entity.Attendance;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 근태 정보 응답 DTO
 * 근태 정보 조회 시 사용됩니다
 */
public record AttendanceDto(
        Long id,
        EmployeeDto employee,
        LocalDate attendanceDate,
        LocalDateTime checkInTime,
        LocalDateTime checkOutTime,
        LocalDateTime breakStartTime,
        LocalDateTime breakEndTime,
        Integer workMinutes,
        Integer overtimeMinutes,
        Integer nightWorkMinutes,
        Integer holidayWorkMinutes,
        Integer lateMinutes,
        Integer earlyLeaveMinutes,
        Attendance.AttendanceStatus attendanceStatus,
        Attendance.WorkType workType,
        String checkInLocation,
        String checkOutLocation,
        String remarks,
        EmployeeDto approvedBy,
        LocalDateTime approvedAt,
        Attendance.ApprovalStatus approvalStatus,
        String workTimeFormatted,
        String overtimeFormatted,
        Boolean isNormalAttendance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public AttendanceDto {
        if (employee == null) {
            throw new IllegalArgumentException("직원 정보는 필수입니다");
        }
        if (attendanceDate == null) {
            throw new IllegalArgumentException("근무일은 필수입니다");
        }
        if (attendanceStatus == null) {
            throw new IllegalArgumentException("근태 상태는 필수입니다");
        }
    }
    
    /**
     * Attendance 엔티티로부터 AttendanceDto 생성
     */
    public static AttendanceDto from(Attendance attendance) {
        return new AttendanceDto(
            attendance.getId(),
            attendance.getEmployee() != null ? EmployeeDto.from(attendance.getEmployee()) : null,
            attendance.getAttendanceDate(),
            attendance.getCheckInTime(),
            attendance.getCheckOutTime(),
            attendance.getBreakStartTime(),
            attendance.getBreakEndTime(),
            attendance.getWorkMinutes(),
            attendance.getOvertimeMinutes(),
            attendance.getNightWorkMinutes(),
            attendance.getHolidayWorkMinutes(),
            attendance.getLateMinutes(),
            attendance.getEarlyLeaveMinutes(),
            attendance.getAttendanceStatus(),
            attendance.getWorkType(),
            attendance.getCheckInLocation(),
            attendance.getCheckOutLocation(),
            attendance.getRemarks(),
            attendance.getApprovedBy() != null ? EmployeeDto.from(attendance.getApprovedBy()) : null,
            attendance.getApprovedAt(),
            attendance.getApprovalStatus(),
            attendance.getWorkTimeFormatted(),
            attendance.getOvertimeFormatted(),
            attendance.isNormalAttendance(),
            attendance.getCreatedAt(),
            attendance.getUpdatedAt()
        );
    }
}




