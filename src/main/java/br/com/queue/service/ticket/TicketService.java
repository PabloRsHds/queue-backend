package br.com.queue.service.ticket;

import br.com.queue.dtos.ticket.allTickets.ResponseAllTicketsDto;
import br.com.queue.dtos.ticket.callTicket.CallTicketDto;
import br.com.queue.dtos.ticket.callTicket.ResponseCallTicketDto;
import br.com.queue.dtos.ticket.create.CreateTicketDto;
import br.com.queue.dtos.ticket.create.ResponseTicketDto;
import br.com.queue.dtos.ticket.finishTicket.FinishTicketDto;
import br.com.queue.dtos.ticket.finishTicket.ResponseFinishTicketDto;
import br.com.queue.dtos.ticket.startAttendance.ResponseStartAttendanceDto;
import br.com.queue.dtos.ticket.startAttendance.StartAttendanceDto;
import br.com.queue.entities.schedule.Schedule;
import br.com.queue.entities.ticket.Ticket;
import br.com.queue.entities.user.User;
import br.com.queue.enums.PriorityLevel;
import br.com.queue.enums.TicketStatus;
import br.com.queue.repositories.customer.CustomerRepository;
import br.com.queue.repositories.schedule.ScheduleRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import br.com.queue.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final ServiceManagementRepository serviceManagementRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ResponseTicketDto createTicket(CreateTicketDto dto) {

        var customer = this.customerRepository.findByCustomerId(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        var service = this.serviceManagementRepository
                .findByServiceManagementId(dto.serviceManagementId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        var entity = new Ticket();

        entity.setCode(generateCode());
        entity.setCustomer(customer);
        entity.setServiceManagement(service);
        entity.setPriority(PriorityLevel.valueOf(dto.priority()));
        entity.setStatus(TicketStatus.WAITING);
        entity.setCreatedAt(LocalDateTime.now());

        if (dto.scheduleId() != null) {

            Schedule schedule = this.scheduleRepository.findById(dto.scheduleId())
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

            entity.setSchedule(schedule);
        }

        this.ticketRepository.save(entity);

        return new ResponseTicketDto(
                entity.getTicketId(),
                entity.getCode(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                entity.getPriority().name(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getCalledAt(),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    @Transactional
    public ResponseCallTicketDto callTicket(CallTicketDto dto) {

        Ticket ticket = this.ticketRepository.findByTicketId(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        ticket.setStatus(TicketStatus.CALLED);
        ticket.setCalledAt(LocalDateTime.now());

        this.ticketRepository.save(ticket);

        return new ResponseCallTicketDto(
                ticket.getTicketId(),
                ticket.getCode(),
                ticket.getCustomer().getCustomerId(),
                ticket.getCustomer().getName(),
                ticket.getServiceManagement().getServiceManagementId(),
                ticket.getServiceManagement().getName(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getCalledAt(),
                ticket.getStartedAt(),
                ticket.getFinishedAt()
        );
    }

    @Transactional
    public ResponseStartAttendanceDto startAttendance(StartAttendanceDto dto) {

        Ticket ticket = this.ticketRepository.findById(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        User attendant = this.userRepository.findById(dto.attendantId())
                .orElseThrow(() -> new EntityNotFoundException("Attendant not found"));

        ticket.setAttendant(attendant);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setStartedAt(LocalDateTime.now());

        this.ticketRepository.save(ticket);

        return new ResponseStartAttendanceDto(
                ticket.getTicketId(),
                ticket.getCode(),
                ticket.getCustomer().getCustomerId(),
                ticket.getCustomer().getName(),
                ticket.getServiceManagement().getServiceManagementId(),
                ticket.getServiceManagement().getName(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getCalledAt(),
                ticket.getStartedAt(),
                ticket.getFinishedAt()
        );
    }

    @Transactional
    public ResponseFinishTicketDto finishTicket(FinishTicketDto dto) {

        Ticket ticket = this.ticketRepository.findById(dto.ticketId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        ticket.setStatus(TicketStatus.valueOf(dto.status()));
        ticket.setFinishedAt(LocalDateTime.now());

        this.ticketRepository.save(ticket);

        return new ResponseFinishTicketDto(
                ticket.getTicketId(),
                ticket.getCode(),
                ticket.getCustomer().getCustomerId(),
                ticket.getCustomer().getName(),
                ticket.getServiceManagement().getServiceManagementId(),
                ticket.getServiceManagement().getName(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getCalledAt(),
                ticket.getStartedAt(),
                ticket.getFinishedAt()
        );
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

    public ResponseTicketDto getTicketById(String ticketId) {

        Ticket ticket = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        return new ResponseTicketDto(
                ticket.getTicketId(),
                ticket.getCode(),
                ticket.getCustomer().getCustomerId(),
                ticket.getCustomer().getName(),
                ticket.getServiceManagement().getServiceManagementId(),
                ticket.getServiceManagement().getName(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getCreatedAt(),
                ticket.getCalledAt(),
                ticket.getStartedAt(),
                ticket.getFinishedAt()
        );
    }

    @Transactional
    public void deleteTicket(String ticketId) {

        Ticket ticket = this.ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        this.ticketRepository.delete(ticket);
    }

    private String generateCode() {

        return "TCK-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}