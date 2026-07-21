package br.com.queue.service.attendance;

import br.com.queue.dtos.attendance.allAttendances.ResponseAllAttendances;
import br.com.queue.dtos.attendance.start.StartAttendanceDto;
import br.com.queue.dtos.attendance.start.FinishAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseFinishAttendanceDto;
import br.com.queue.dtos.attendance.statistics.ResponseAttendanceDashboardDto;
import br.com.queue.entities.attendance.Attendance;
import br.com.queue.enums.Role;
import br.com.queue.enums.TicketStatus;
import br.com.queue.repositories.attendance.AttendanceRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import br.com.queue.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseAttendanceDto startAttendance(JwtAuthenticationToken token, StartAttendanceDto dto) {

        var ticket = this.ticketRepository.findById(dto.ticketId())
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

    public ResponseAttendanceDashboardDto getAttendanceStatistics() {

        return new ResponseAttendanceDashboardDto(

                attendanceRepository.countTotalAttendances(),

                attendanceRepository.getAverageWaitingTime(),

                attendanceRepository.getAverageServiceTime(),

                attendanceRepository.averageAttendanceByUser(),

                attendanceRepository.countAttendancesCreatedByMonth(),

                attendanceRepository.countAttendancesByWeek(),

                attendanceRepository.countAttendancesByService(),

                attendanceRepository.countAttendancesByHour(),

                attendanceRepository.countAttendancesByDepartment(),

                attendanceRepository.countAttendancesByCustomer()

        );
    }
}
