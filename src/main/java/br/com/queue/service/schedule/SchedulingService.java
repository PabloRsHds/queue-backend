package br.com.queue.service.schedule;

import br.com.queue.dtos.schedule.allSchedules.ResponseAllSchedulesDto;
import br.com.queue.dtos.schedule.create.CreateScheduleDto;
import br.com.queue.dtos.schedule.create.ResponseScheduleDto;
import br.com.queue.dtos.schedule.statistics.ResponseScheduleStatisticsDto;
import br.com.queue.dtos.schedule.update.UpdateScheduleDto;
import br.com.queue.entities.schedule.Schedule;
import br.com.queue.entities.ticket.Ticket;
import br.com.queue.enums.PriorityLevel;
import br.com.queue.enums.ScheduleStatus;
import br.com.queue.repositories.customer.CustomerRepository;
import br.com.queue.repositories.schedule.ScheduleRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import br.com.queue.repositories.ticket.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ScheduleRepository scheduleRepository;
    private final CustomerRepository customerRepository;
    private final ServiceManagementRepository serviceManagementRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public ResponseScheduleDto createSchedule(CreateScheduleDto dto) {

        var customer = this.customerRepository.findByCustomerId(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        var service = this.serviceManagementRepository.findById(dto.serviceManagementId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        var entity = new Schedule();

        entity.setCustomer(customer);
        entity.setServiceManagement(service);
        entity.setPriority(PriorityLevel.valueOf(dto.priority()));
        entity.setScheduledDate(dto.scheduledDate());
        entity.setStatus(ScheduleStatus.SCHEDULED);
        entity.setCreatedAt(LocalDateTime.now());

        this.scheduleRepository.save(entity);

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        var ticketId = "";
        var ticketCode = "";

        if (entity.getTicket() != null) {
            ticketId = entity.getTicket().getTicketId();

            var ticket = this.ticketRepository.findByTicketId(ticketId).orElseThrow();
            ticketCode = ticket.getCode();

        } else {
            ticketId = null;
        }

        return new ResponseScheduleDto(
                entity.getScheduleId(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                ticketId,
                ticketCode,
                entity.getPriority().name(),
                entity.getScheduledDate(),
                entity.getStatus().name(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );
    }

    @Transactional
    public ResponseScheduleDto updateSchedule(UpdateScheduleDto dto) {

        var schedule = this.scheduleRepository.findById(dto.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        if (!dto.customerId().isBlank()) {
            var customer = this.customerRepository
                    .findByCustomerId(dto.customerId())
                    .orElseThrow();

            schedule.setCustomer(customer);
        }

        if (!dto.serviceManagementId().isBlank()) {
            var serviceManagement = this.serviceManagementRepository
                    .findByServiceManagementId(dto.serviceManagementId())
                    .orElseThrow();
            schedule.setServiceManagement(serviceManagement);
        }

        if (dto.priority() != null) {
            schedule.setPriority(PriorityLevel.valueOf(dto.priority()));
        }

        if (dto.scheduledDate() != null) {
            schedule.setScheduledDate(dto.scheduledDate());
        }

        if (!dto.status().isBlank()) {
            schedule.setStatus(ScheduleStatus.valueOf(dto.status()));
        }

        var ticketId = "";
        var ticketCode = "";

        if (schedule.getTicket() != null) {
            ticketId = schedule.getTicket().getTicketId();

            var ticket = this.ticketRepository.findByTicketId(ticketId).orElseThrow();
            ticketCode = ticket.getCode();

        } else {
            ticketId = null;
        }

        schedule.setUpdatedAt(LocalDateTime.now());
        this.scheduleRepository.save(schedule);

        return new ResponseScheduleDto(
                schedule.getScheduleId(),
                schedule.getCustomer().getCustomerId(),
                schedule.getCustomer().getName(),
                schedule.getServiceManagement().getServiceManagementId(),
                schedule.getServiceManagement().getName(),
                ticketId,
                ticketCode,
                schedule.getPriority().name(),
                schedule.getScheduledDate(),
                schedule.getStatus().name(),
                schedule.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                schedule.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    public Page<ResponseAllSchedulesDto> getAllSchedules(int page, int size, String search, LocalDate scheduleDate) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.scheduleRepository.findAllWithSearch(
                normalizedSearch,
                scheduleDate,
                PageRequest.of(page, size)
        );
    }

    public ResponseScheduleDto getScheduleById(String scheduleId) {

        var entity = this.scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        var ticketId = "";
        var ticketCode = "";

        if (entity.getTicket() != null) {
            ticketId = entity.getTicket().getTicketId();

            var ticket = this.ticketRepository.findByTicketId(ticketId).orElseThrow();
            ticketCode = ticket.getCode();

        } else {
            ticketId = null;
        }

        return new ResponseScheduleDto(
                entity.getScheduleId(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                ticketId,
                ticketCode,
                entity.getPriority().name(),
                entity.getScheduledDate(),
                entity.getStatus().name(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );
    }

    @Transactional
    public ResponseScheduleDto deleteSchedule(String scheduleId) {

        var entity = this.scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        var ticketId = "";
        var ticketCode = "";

        if (entity.getTicket() != null) {
            ticketId = entity.getTicket().getTicketId();

            var ticket = this.ticketRepository.findByTicketId(ticketId).orElseThrow();
            ticketCode = ticket.getCode();

        } else {
            ticketId = null;
        }

        var response = new ResponseScheduleDto(
                entity.getScheduleId(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                ticketId,
                ticketCode,
                entity.getPriority().name(),
                entity.getScheduledDate(),
                entity.getStatus().name(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );

        this.scheduleRepository.delete(entity);
        return response;
    }

    // Estatisticas
    public ResponseScheduleStatisticsDto getScheduleStatistics() {
        return this.scheduleRepository.getScheduleStatistics();
    }
}