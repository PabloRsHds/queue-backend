package br.com.queue.service.attendance;

import br.com.queue.dtos.attendance.allAttendances.ResponseAllAttendances;
import br.com.queue.dtos.attendance.create.CreateAttendanceDto;
import br.com.queue.dtos.attendance.create.FinishAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseFinishAttendanceDto;
import br.com.queue.entities.attendance.Attendance;
import br.com.queue.repositories.attendance.AttendanceRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public ResponseAttendanceDto createAttendance(CreateAttendanceDto dto) {

        var ticket = this.ticketRepository.findById(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        var entity = new Attendance();

        entity.setTicket(ticket);
        entity.setObservation(dto.observation());
        entity.setResolution(dto.resolution());
        entity.setStartedAt(LocalDateTime.now());
        this.attendanceRepository.save(entity);

        return new ResponseAttendanceDto(
                entity.getTicket().getTicketId(),
                entity.getTicket().getCode(),
                entity.getObservation(),
                entity.getResolution(),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    @Transactional
    public ResponseFinishAttendanceDto finishAttendance(FinishAttendanceDto dto) {

        Attendance attendance = this.attendanceRepository.findByAttendanceId(dto.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));

        attendance.setResolution(dto.resolution());
        attendance.setObservation(dto.observation());
        attendance.setFinishedAt(LocalDateTime.now());
        this.attendanceRepository.save(attendance);

        return new ResponseFinishAttendanceDto(
                attendance.getResolution(),
                attendance.getObservation(),
                attendance.getFinishedAt()
        );
    }

    public Page<ResponseAllAttendances> getAllAttendances(int page, int size) {
        return this.attendanceRepository.findAll(PageRequest.of(page, size))
                .map(attendance -> new ResponseAllAttendances(
                        attendance.getTicket().getTicketId(),
                        attendance.getTicket().getCode(),
                        attendance.getObservation(),
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
}
