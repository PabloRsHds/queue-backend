package br.com.queue.service.schedule;

import br.com.queue.dtos.schedule.allSchedules.ResponseAllSchedulesDto;
import br.com.queue.dtos.schedule.create.CreateScheduleDto;
import br.com.queue.dtos.schedule.create.ResponseScheduleDto;
import br.com.queue.dtos.schedule.update.ResponseUpdateScheduleDto;
import br.com.queue.dtos.schedule.update.UpdateScheduleDto;
import br.com.queue.entities.schedule.Schedule;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.enums.ScheduleStatus;
import br.com.queue.repositories.customer.CustomerRepository;
import br.com.queue.repositories.schedule.ScheduleRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ScheduleRepository scheduleRepository;
    private final CustomerRepository customerRepository;
    private final ServiceManagementRepository serviceManagementRepository;

    @Transactional
    public ResponseScheduleDto createSchedule(CreateScheduleDto dto) {

        var customer = this.customerRepository.findByCustomerId(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        ServiceManagement service = this.serviceManagementRepository.findById(dto.serviceManagementId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        var entity = new Schedule();

        entity.setCustomer(customer);
        entity.setServiceManagement(service);
        entity.setScheduledDate(dto.scheduledDate());
        entity.setStatus(ScheduleStatus.SCHEDULED);
        entity.setNotes(dto.notes());
        entity.setCreatedAt(LocalDateTime.now());

        this.scheduleRepository.save(entity);

        return new ResponseScheduleDto(
                entity.getScheduleId(),
                entity.getCustomer().getCustomerId(),
                entity.getCustomer().getName(),
                entity.getServiceManagement().getServiceManagementId(),
                entity.getServiceManagement().getName(),
                entity.getScheduledDate(),
                entity.getStatus().name(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }

    @Transactional
    public ResponseUpdateScheduleDto updateSchedule(UpdateScheduleDto dto) {

        Schedule schedule = this.scheduleRepository.findById(dto.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        schedule.setStatus(ScheduleStatus.valueOf(dto.status()));
        this.scheduleRepository.save(schedule);

        return new ResponseUpdateScheduleDto(
                schedule.getScheduleId(),
                schedule.getCustomer().getCustomerId(),
                schedule.getCustomer().getName(),
                schedule.getServiceManagement().getServiceManagementId(),
                schedule.getServiceManagement().getName(),
                schedule.getScheduledDate(),
                schedule.getStatus().name(),
                schedule.getNotes(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
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

        Schedule schedule = this.scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        return new ResponseScheduleDto(
                schedule.getScheduleId(),
                schedule.getCustomer().getCustomerId(),
                schedule.getCustomer().getName(),
                schedule.getServiceManagement().getServiceManagementId(),
                schedule.getServiceManagement().getName(),
                schedule.getScheduledDate(),
                schedule.getStatus().name(),
                schedule.getNotes(),
                schedule.getCreatedAt()
        );
    }

    @Transactional
    public void deleteSchedule(String scheduleId) {

        var schedule = this.scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        this.scheduleRepository.delete(schedule);
    }
}