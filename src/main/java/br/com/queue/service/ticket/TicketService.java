package br.com.queue.service.ticket;

import br.com.queue.dtos.ticket.ResponseTicketDto;
import br.com.queue.dtos.ticket.allTickets.ResponseAllTicketsDto;
import br.com.queue.dtos.ticket.attendance.ResponseTicketsForAttendance;
import br.com.queue.dtos.ticket.callTicket.CallTicketDto;
import br.com.queue.dtos.ticket.create.CreateTicketDto;
import br.com.queue.dtos.ticket.finishTicket.FinishTicketDto;
import br.com.queue.entities.attendance.Attendance;
import br.com.queue.entities.customer.Customer;
import br.com.queue.entities.schedule.Schedule;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.entities.ticket.Ticket;
import br.com.queue.enums.PriorityLevel;
import br.com.queue.enums.TicketStatus;
import br.com.queue.repositories.attendance.AttendanceRepository;
import br.com.queue.repositories.customer.CustomerRepository;
import br.com.queue.repositories.schedule.ScheduleRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final ServiceManagementRepository serviceManagementRepository;
    private final ScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public ResponseTicketDto createTicket(CreateTicketDto dto) {

        Optional<Schedule> scheduleEntity = this.scheduleRepository.findById(dto.scheduleId());
        Optional<Customer> customerEntity = this.customerRepository.findByCustomerId(dto.customerId());
        Optional<ServiceManagement> serviceManagementEntity = this.serviceManagementRepository
                .findByServiceManagementId(dto.serviceManagementId());

        if (scheduleEntity.isEmpty()
                || customerEntity.isEmpty()
                || serviceManagementEntity.isEmpty()) {
            throw new EntityNotFoundException("Dados não encontrados para dar prosseguimento a criação de ticket");
        }

        var schedule = scheduleEntity.get();
        var customer = customerEntity.get();
        var serviceManagement = serviceManagementEntity.get();

        Optional<Ticket> ticketEntity = this.ticketRepository
                .findTicketByScheduleScheduleId(dto.scheduleId());

        if (ticketEntity.isPresent()) {

            var ticket = ticketEntity.get();
            ticket.setCreatedAt(LocalDateTime.now());
            this.ticketRepository.save(ticket);
            return buildResponseTicketDto(ticket);
        }

        Long nextCallNumber = ticketRepository.getNextCallNumber();

        var entity = new Ticket();
        entity.setCallNumber(nextCallNumber);
        entity.setCode(generateCode(serviceManagement.getCode(), nextCallNumber));
        entity.setCustomer(customer);
        entity.setServiceManagement(serviceManagement);
        entity.setPriority(PriorityLevel.valueOf(dto.priority()));
        entity.setStatus(TicketStatus.WAITING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSchedule(schedule);

        this.ticketRepository.save(entity);

        return buildResponseTicketDto(entity);
    }

    @Transactional
    public ResponseTicketDto callTicket(CallTicketDto dto) {

        var entity = this.ticketRepository.findByTicketId(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        entity.setStatus(TicketStatus.CALLED);
        entity.setCalledAt(LocalDateTime.now());

        this.ticketRepository.save(entity);

        return this.buildResponseTicketDto(entity);
    }

    @Transactional
    public ResponseTicketDto finishTicket(FinishTicketDto dto) {

        var entity = this.ticketRepository.findById(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        entity.setStatus(TicketStatus.valueOf(dto.status()));

        this.ticketRepository.save(entity);

        return this.buildResponseTicketDto(entity);
    }

    public Page<ResponseAllTicketsDto> getAllTickets(int page, int size) {

        return this.ticketRepository.findAll(PageRequest.of(page, size))
                .map(ticket -> new ResponseAllTicketsDto(
                        ticket.getTicketId(),
                        ticket.getCode(),
                        ticket.getCustomer().getName(),
                        ticket.getServiceManagement().getName(),
                        ticket.getPriority().name(),
                        ticket.getStatus().name(),
                        ticket.getCreatedAt()
                ));
    }

    public Page<ResponseTicketsForAttendance> getTicketsByAttendant(
            JwtAuthenticationToken token,
            int page,
            int size
    ) {

        return this.ticketRepository
                .getTicketsByAttendant(token.getName(), PageRequest.of(page, size))
                .map(ticket -> {

                    Attendance attendance = null;
                    var attendanceTime = "00:00:00";
                    LocalDateTime startedAt = null;
                    LocalDateTime finishedAt = null;

                    if (ticket.getAttendance() == null) {
                        
                    } else {
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

                        return new ResponseTicketsForAttendance(
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
                        );
                    }

                    return new ResponseTicketsForAttendance(
                            ticket.getTicketId(),
                            ticket.getCode(),
                            ticket.getStatus().name(),
                            ticket.getPriority().name(),
                            ticket.getCustomer().getName(),
                            ticket.getServiceManagement().getName(),
                            ticket.getCreatedAt(),
                            null,
                            null,
                            attendanceTime
                    );
                });
    }

    public Page<ResponseTicketsForAttendance> getHistoryTicketsByAttendant(
            JwtAuthenticationToken token,
            int page,
            int size
    ) {
        return this.ticketRepository
                .getHistoryTicketsByAttendant(token.getName(), PageRequest.of(page, size))
                .map(ticket -> {

                    Attendance attendance = null;
                    var attendanceTime = "00:00:00";
                    LocalDateTime startedAt = null;
                    LocalDateTime finishedAt = null;

                    if (ticket.getAttendance() == null) {

                    } else {
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

                        return new ResponseTicketsForAttendance(
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
                        );
                    }

                    return new ResponseTicketsForAttendance(
                            ticket.getTicketId(),
                            ticket.getCode(),
                            ticket.getStatus().name(),
                            ticket.getPriority().name(),
                            ticket.getCustomer().getName(),
                            ticket.getServiceManagement().getName(),
                            ticket.getCreatedAt(),
                            null,
                            null,
                            attendanceTime
                    );
                });
    }

    public ResponseTicketDto getTicketById(String ticketId) {

        var entity = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        return buildResponseTicketDto(entity);
    }

    @Transactional
    public ResponseTicketDto deleteTicket(String ticketId) {

        var entity = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        var response = this.buildResponseTicketDto(entity);

        if (entity.getAttendance() != null) {
            this.attendanceRepository.delete(entity.getAttendance());
        }

        this.ticketRepository.delete(entity);

        return response;
    }

    public ResponseTicketDto cancelTicket(String ticketId) {

        var entity = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        entity.setStatus(TicketStatus.CANCELED);
        this.ticketRepository.save(entity);

        return this.buildResponseTicketDto(entity);
    }

    public void resetCode(String ticketId) {

        var entity = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        entity.setCallNumber(0);
        this.ticketRepository.save(entity);
    }

    private String generateCode(String prefix, long callNumber) {

        if (callNumber < 10) {
            return "%s-%02d".formatted(prefix, callNumber);
        } else if (callNumber < 100) {
            return "%s-%03d".formatted(prefix, callNumber);
        }
        return "%s-%03d".formatted(prefix, callNumber);
    }

    // Metodo auxiliar para não repetir código
    private ResponseTicketDto buildResponseTicketDto(Ticket entity) {
        var calledAt = "";

        if (entity.getCalledAt() != null) {
            calledAt = entity.getCalledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        return new ResponseTicketDto(
                entity.getTicketId(),
                entity.getCode(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                entity.getPriority().name(),
                entity.getStatus().name(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                calledAt
        );
    }
}