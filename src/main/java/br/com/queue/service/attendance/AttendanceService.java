package br.com.queue.service.attendance;

import br.com.queue.dtos.attendance.allAttendances.ResponseAllAttendances;
import br.com.queue.dtos.attendance.start.StartAttendanceDto;
import br.com.queue.dtos.attendance.start.FinishAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseFinishAttendanceDto;
import br.com.queue.dtos.attendance.statistics.ResponseAttendanceDashboardDto;
import br.com.queue.dtos.ticket.attendance.ResponseTicketsForAttendance;
import br.com.queue.entities.attendance.Attendance;
import br.com.queue.enums.Role;
import br.com.queue.enums.TicketStatus;
import br.com.queue.repositories.attendance.AttendanceRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import br.com.queue.repositories.user.UserRepository;
import br.com.queue.service.unit.UnitContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final UnitContext unitContext;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ResponseAttendanceDto startAttendance(JwtAuthenticationToken token, StartAttendanceDto dto) {

        var unit = this.unitContext.getCurrentUnit(token);

        var ticket = this.ticketRepository.findByTicketIdAndUnitId(dto.ticketId(), unit.getUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        var user = this.userRepository.findById(token.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() != Role.ATTENDANT
                && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException(
                    "User is not allowed to start attendances");
        }

        if (ticket.getStatus() != TicketStatus.WAITING) {
            throw new IllegalStateException(
                    "Only called tickets can start attendance");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);

        var attendance = new Attendance();

        attendance.setTicket(ticket);
        attendance.setUser(user);
        attendance.setStartedAt(LocalDateTime.now());
        attendance.setUnit(unit);

        ticketRepository.save(ticket);
        attendanceRepository.save(attendance);

        return new ResponseAttendanceDto(
                attendance.getTicket().getTicketId(),
                attendance.getTicket().getCode(),
                attendance.getStartedAt()
        );
    }

    @Transactional
    public ResponseFinishAttendanceDto finishAttendance(FinishAttendanceDto dto) {

        var ticket = this.ticketRepository.findByTicketId(dto.ticketId())
                        .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        var attendance = this.attendanceRepository.findByTicket(ticket)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));

        attendance.setResolution(dto.resolution());
        attendance.setFinishedAt(LocalDateTime.now());

        ticket.setStatus(TicketStatus.FINISHED);

        this.attendanceRepository.save(attendance);
        this.ticketRepository.save(ticket);

        var attendanceTime = "00:00:00";
        LocalDateTime startedAt = null;
        LocalDateTime finishedAt = null;

        attendance = ticket.getAttendance();

        if (attendance.getFinishedAt() != null) {
            Duration duration = Duration.between(
                    attendance.getStartedAt(),
                    attendance.getFinishedAt()
            );

            long seconds = duration.getSeconds();

            attendanceTime = String.format(
                    "%02d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
            );
        }

        if (attendance.getStartedAt() != null) {
            startedAt = attendance.getStartedAt();
        }

        if (attendance.getFinishedAt() != null) {
            finishedAt = attendance.getFinishedAt();
        }

        messagingTemplate.convertAndSend(
                "/topic/tickets/history",
                new ResponseTicketsForAttendance(
                        ticket.getTicketId(),
                        ticket.getCode(),
                        ticket.getStatus().name(),
                        ticket.getPriority().name(),
                        ticket.getCustomer().getName(),
                        ticket.getServiceManagement().getName(),
                        ticket.getCreatedAt(),
                        startedAt,
                        finishedAt,
                        attendanceTime
                )
        );

        return new ResponseFinishAttendanceDto(
                attendance.getResolution(),
                attendance.getFinishedAt()
        );
    }

    public Page<ResponseAllAttendances> getAllAttendances(int page, int size) {
        return this.attendanceRepository.findAll(PageRequest.of(page, size))
                .map(attendance -> new ResponseAllAttendances(
                        attendance.getTicket().getTicketId(),
                        attendance.getTicket().getCode(),
                        attendance.getResolution(),
                        attendance.getStartedAt(),
                        attendance.getFinishedAt()
                ));
    }

    @Transactional
    public void deleteAttendance(String attendanceId) {

        Attendance attendance = this.attendanceRepository.findByAttendanceId(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));

        this.attendanceRepository.delete(attendance);
    }

    public ResponseAttendanceDashboardDto getAttendanceStatistics(JwtAuthenticationToken token) {

        var unit = this.unitContext.getCurrentUnit(token);

        return new ResponseAttendanceDashboardDto(

                attendanceRepository.countTotalAttendances(unit.getUnitId()),
                attendanceRepository.getAverageWaitingTime(unit.getUnitId()),
                attendanceRepository.getAverageServiceTime(unit.getUnitId()),
                attendanceRepository.averageAttendanceByUser(unit.getUnitId()),
                attendanceRepository.countAttendancesCreatedByMonth(unit.getUnitId()),
                attendanceRepository.countAttendancesByWeek(unit.getUnitId()),
                attendanceRepository.countAttendancesByService(unit.getUnitId()),
                attendanceRepository.countAttendancesByHour(unit.getUnitId()),
                attendanceRepository.countAttendancesByDepartment(unit.getUnitId()),
                attendanceRepository.countAttendancesByCustomer(unit.getUnitId())

        );
    }
}
