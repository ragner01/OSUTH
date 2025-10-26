package ng.osun.his.staffrota.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.staffrota.domain.*;
import ng.osun.his.staffrota.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service for rota management with conflict detection.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RotaService {

    private final RotaRepository rotaRepository;
    private final StaffRepository staffRepository;
    private final ShiftTemplateRepository shiftTemplateRepository;
    private final LeaveRepository leaveRepository;

    /**
     * Generate rota for period with conflict detection.
     */
    @Transactional
    public Rota generateRota(String department, LocalDate periodStart, LocalDate periodEnd, String createdBy) {
        log.info("Generating rota for {} from {} to {}", department, periodStart, periodEnd);

        // Check for existing rota
        if (rotaRepository.existsByDepartmentAndPeriodStartAndPeriodEnd(department, periodStart, periodEnd)) {
            throw new IllegalStateException("Rota already exists for this period");
        }

        // Get staff for department
        List<Staff> staff = staffRepository.findByDepartmentAndActiveTrue(department);
        if (staff.isEmpty()) {
            throw new IllegalStateException("No active staff found in department");
        }

        // Get shift templates
        List<ShiftTemplate> templates = shiftTemplateRepository.findByDepartment(department);

        // Get approved leaves
        List<Leave> approvedLeaves = leaveRepository.findByStatus("APPROVED")
            .stream()
            .filter(l -> l.getStartDate().isAfter(periodStart.minusDays(1)) && 
                        l.getStartDate().isBefore(periodEnd.plusDays(1)))
            .collect(Collectors.toList());

        // Create rota
        Rota rota = new Rota();
        rota.setRotaPeriod("ROTA-" + periodStart + "-" + periodEnd);
        rota.setPeriodStart(periodStart);
        rota.setPeriodEnd(periodEnd);
        rota.setDepartment(department);
        rota.setStatus("DRAFT");
        rota.setCreatedBy(createdBy);

        // Generate shifts (simplified - assign staff to shifts based on templates)
        List<RotaShift> shifts = templates.stream()
            .flatMap(template -> generateShiftsForTemplate(template, periodStart, periodEnd, staff, approvedLeaves))
            .collect(Collectors.toList());

        rota.setShifts(shifts);
        
        // Detect conflicts
        List<String> conflicts = detectConflicts(shifts, approvedLeaves);
        if (!conflicts.isEmpty()) {
            rota.setNotes("WARNINGS: " + String.join(", ", conflicts));
            log.warn("Rota has conflicts: {}", conflicts);
        }

        Rota saved = rotaRepository.save(rota);
        log.info("Rota {} generated with {} shifts", saved.getId(), shifts.size());
        return saved;
    }

    /**
     * Publish rota (finalize).
     */
    @Transactional
    public void publishRota(String rotaId, String approvedBy) {
        Rota rota = rotaRepository.findById(rotaId)
            .orElseThrow(() -> new IllegalArgumentException("Rota not found"));

        rota.setStatus("PUBLISHED");
        rota.setApprovedBy(approvedBy);
        rota.setApprovedAt(java.time.Instant.now());

        rotaRepository.save(rota);
        log.info("Rota {} published", rotaId);
    }

    private java.util.stream.Stream<RotaShift> generateShiftsForTemplate(ShiftTemplate template, 
                                                                          LocalDate periodStart, LocalDate periodEnd,
                                                                          List<Staff> staff, List<Leave> leaves) {
        // Simple assignment - rotate staff based on shift type
        final AtomicInteger index = new AtomicInteger(0);
        final int staffSize = staff.size();
        return periodStart.datesUntil(periodEnd.plusDays(1))
            .filter(date -> {
                int currentIndex = index.get();
                return !isLeaveDate(staff.get(currentIndex % staffSize).getId(), date, leaves);
            })
            .map(date -> {
                int currentIndex = index.getAndIncrement();
                Staff assignedStaff = staff.get(currentIndex % staffSize);
                
                RotaShift shift = new RotaShift();
                shift.setStaffId(assignedStaff.getId());
                shift.setShiftDate(date);
                shift.setStartTime(template.getStartTime());
                shift.setEndTime(template.getEndTime());
                shift.setShiftType(template.getShiftType());
                shift.setStatus("ASSIGNED");
                return shift;
            });
    }

    private boolean isLeaveDate(String staffId, LocalDate date, List<Leave> leaves) {
        return leaves.stream()
            .anyMatch(l -> l.getStaffId().equals(staffId) && 
                          !date.isBefore(l.getStartDate()) && 
                          !date.isAfter(l.getEndDate()));
    }

    private List<String> detectConflicts(List<RotaShift> shifts, List<Leave> leaves) {
        List<String> conflicts = new java.util.ArrayList<>();
        
        // Detect overlapping shifts for same staff
        shifts.stream()
            .collect(Collectors.groupingBy(RotaShift::getStaffId))
            .forEach((staffId, staffShifts) -> {
                for (int i = 0; i < staffShifts.size(); i++) {
                    for (int j = i + 1; j < staffShifts.size(); j++) {
                        RotaShift s1 = staffShifts.get(i);
                        RotaShift s2 = staffShifts.get(j);
                        if (s1.getShiftDate().equals(s2.getShiftDate()) && 
                            shiftsOverlap(s1, s2)) {
                            conflicts.add("Staff " + staffId + " has overlapping shifts on " + s1.getShiftDate());
                        }
                    }
                }
            });

        return conflicts;
    }

    private boolean shiftsOverlap(RotaShift s1, RotaShift s2) {
        return s1.getStartTime().isBefore(s2.getEndTime()) && 
               s2.getStartTime().isBefore(s1.getEndTime());
    }
}

