package com.erp.hr.repository;

import com.erp.hr.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 근태 레포지토리
 * 근태 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * 직원과 날짜로 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE a.employee.id = :employeeId AND a.attendanceDate = :date")
    Optional<Attendance> findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, 
                                                @Param("date") LocalDate date);

    /**
     * 직원별 근태 목록 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE a.employee.id = :employeeId " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 직원별 기간별 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE a.employee.id = :employeeId " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.attendanceDate")
    List<Attendance> findByEmployeeIdAndDateBetween(@Param("employeeId") Long employeeId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 부서별 특정 날짜 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE e.department.id = :departmentId AND a.attendanceDate = :date " +
           "ORDER BY e.employeeNumber")
    List<Attendance> findByDepartmentIdAndDate(@Param("departmentId") Long departmentId, 
                                              @Param("date") LocalDate date);

    /**
     * 회사별 특정 날짜 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE e.company.id = :companyId AND a.attendanceDate = :date " +
           "ORDER BY e.employeeNumber")
    List<Attendance> findByCompanyIdAndDate(@Param("companyId") Long companyId, 
                                          @Param("date") LocalDate date);

    /**
     * 특정 날짜 근태 상태별 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE a.attendanceDate = :date AND a.attendanceStatus = :status " +
           "ORDER BY e.employeeNumber")
    List<Attendance> findByDateAndStatus(@Param("date") LocalDate date, 
                                       @Param("status") Attendance.AttendanceStatus status);

    /**
     * 직원별 월별 근태 통계
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "WHERE a.employee.id = :employeeId " +
           "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
           "ORDER BY a.attendanceDate")
    List<Attendance> findByEmployeeIdAndYearMonth(@Param("employeeId") Long employeeId,
                                                 @Param("year") int year,
                                                 @Param("month") int month);

    /**
     * 지각한 직원 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.lateMinutes > 0 " +
           "ORDER BY a.attendanceDate DESC, a.lateMinutes DESC")
    List<Attendance> findLateAttendances(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);

    /**
     * 조퇴한 직원 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.earlyLeaveMinutes > 0 " +
           "ORDER BY a.attendanceDate DESC, a.earlyLeaveMinutes DESC")
    List<Attendance> findEarlyLeaveAttendances(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);

    /**
     * 초과근무한 직원 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.overtimeMinutes > 0 " +
           "ORDER BY a.attendanceDate DESC, a.overtimeMinutes DESC")
    List<Attendance> findOvertimeAttendances(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

    /**
     * 결근한 직원 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "AND a.attendanceStatus = 'ABSENT' " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findAbsentAttendances(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);

    /**
     * 승인 대기 중인 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE a.approvalStatus = 'PENDING' " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findPendingApprovals();

    /**
     * 부서별 승인 대기 중인 근태 조회
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "WHERE e.department.id = :departmentId AND a.approvalStatus = 'PENDING' " +
           "ORDER BY a.attendanceDate DESC")
    List<Attendance> findPendingApprovalsByDepartment(@Param("departmentId") Long departmentId);

    /**
     * 근태 검색 (페이징)
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE (e.name LIKE %:searchTerm% OR e.employeeNumber LIKE %:searchTerm%) " +
           "AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Page<Attendance> searchAttendances(@Param("searchTerm") String searchTerm,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);

    /**
     * 전체 근태 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab")
    Page<Attendance> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 근태 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE e.company.id = :companyId")
    Page<Attendance> findByCompanyIdWithDetails(@Param("companyId") Long companyId, 
                                               Pageable pageable);

    /**
     * 부서별 근태 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH e.company c " +
           "JOIN FETCH e.department d " +
           "JOIN FETCH e.position p " +
           "LEFT JOIN FETCH a.approvedBy ab " +
           "WHERE e.department.id = :departmentId")
    Page<Attendance> findByDepartmentIdWithDetails(@Param("departmentId") Long departmentId, 
                                                  Pageable pageable);

    /**
     * 직원별 근태 중복 확인
     */
    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    /**
     * 직원별 월별 출근 일수 계산
     */
    @Query("SELECT COUNT(a) FROM Attendance a " +
           "WHERE a.employee.id = :employeeId " +
           "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
           "AND a.attendanceStatus IN ('PRESENT', 'LATE', 'EARLY_LEAVE')")
    long countWorkDaysByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                        @Param("year") int year,
                                        @Param("month") int month);

    /**
     * 직원별 월별 총 근무시간 계산
     */
    @Query("SELECT COALESCE(SUM(a.workMinutes), 0) FROM Attendance a " +
           "WHERE a.employee.id = :employeeId " +
           "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
           "AND a.workMinutes IS NOT NULL")
    long sumWorkMinutesByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                         @Param("year") int year,
                                         @Param("month") int month);

    /**
     * 직원별 월별 총 초과근무시간 계산
     */
    @Query("SELECT COALESCE(SUM(a.overtimeMinutes), 0) FROM Attendance a " +
           "WHERE a.employee.id = :employeeId " +
           "AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month " +
           "AND a.overtimeMinutes IS NOT NULL")
    long sumOvertimeMinutesByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                             @Param("year") int year,
                                             @Param("month") int month);

    /**
     * 일별 출근율 통계
     */
    @Query("SELECT a.attendanceDate, " +
           "COUNT(CASE WHEN a.attendanceStatus IN ('PRESENT', 'LATE', 'EARLY_LEAVE') THEN 1 END), " +
           "COUNT(a) " +
           "FROM Attendance a " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY a.attendanceDate " +
           "ORDER BY a.attendanceDate")
    List<Object[]> getAttendanceRateByDate(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);

    /**
     * 부서별 출근율 통계
     */
    @Query("SELECT d.name, " +
           "COUNT(CASE WHEN a.attendanceStatus IN ('PRESENT', 'LATE', 'EARLY_LEAVE') THEN 1 END), " +
           "COUNT(a) " +
           "FROM Attendance a " +
           "JOIN a.employee e " +
           "JOIN e.department d " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY d.id, d.name " +
           "ORDER BY d.name")
    List<Object[]> getAttendanceRateByDepartment(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);

    /**
     * 근태 상태별 통계
     */
    @Query("SELECT a.attendanceStatus, COUNT(a) " +
           "FROM Attendance a " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY a.attendanceStatus")
    List<Object[]> getAttendanceCountByStatus(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);

    /**
     * 근무 유형별 통계
     */
    @Query("SELECT a.workType, COUNT(a) " +
           "FROM Attendance a " +
           "WHERE a.attendanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY a.workType")
    List<Object[]> getAttendanceCountByWorkType(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
}




