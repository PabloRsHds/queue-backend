package br.com.queue.service.serviceManagement;

import br.com.queue.dtos.serviceManagement.ResponseServiceManagementDto;
import br.com.queue.dtos.serviceManagement.create.CreateServiceManagementDto;
import br.com.queue.dtos.serviceManagement.getServiceDto.ResponseGetServiceByIdDto;
import br.com.queue.dtos.serviceManagement.list_service.ResponseServicesForCreatedUser;
import br.com.queue.dtos.serviceManagement.statistics.ResponseServiceDashBoardDto;
import br.com.queue.dtos.serviceManagement.update.UpdateServiceManagementDto;
import br.com.queue.dtos.statistics.ResponseStatisticsDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.repositories.department.DepartmentRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceManagementRepository serviceRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public ResponseServiceManagementDto createServiceManagement(CreateServiceManagementDto dto) {

        var department = this.departmentRepository.findByName(dto.departmentName())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        var entity = new ServiceManagement();

        entity.setName(dto.name());
        entity.setCode(dto.code());
        entity.setDescription(dto.description());
        entity.setDepartment(department);
        entity.setActive(true);
        this.serviceRepository.save(entity);

        return new ResponseServiceManagementDto(
                entity.getServiceManagementId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDepartment().getDepartmentId(),
                entity.getDepartment().getName(),
                entity.getActive()
        );
    }

    @Transactional
    public ResponseServiceManagementDto updateServiceManagement(UpdateServiceManagementDto dto) {

        var service = this.serviceRepository.findByServiceManagementId(dto.serviceManagementId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        var department = this.departmentRepository.findByName(dto.departmentName())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        service.setName(dto.name());
        service.setCode(dto.code());
        service.setDescription(dto.description());
        service.setDepartment(department);
        service.setActive(dto.active());
        service.setUpdatedAt(LocalDateTime.now());
        this.serviceRepository.save(service);

        return new ResponseServiceManagementDto(
                service.getServiceManagementId(),
                service.getName(),
                service.getCode(),
                service.getDescription(),
                service.getDepartment().getDepartmentId(),
                service.getDepartment().getName(),
                service.getActive()
        );
    }

    public Page<ResponseServiceManagementDto> getAllServicesManagement(
            int page,
            int size,
            String search) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.serviceRepository.findAllWithSearch(normalizedSearch,
                PageRequest.of(page, size));
    }

    public List<ResponseServicesForCreatedUser> servicesForCreatedUser() {

        return this.serviceRepository.findAll()
                .stream()
                .filter(service -> service.getActive() == true)
                .map(service ->
                        new ResponseServicesForCreatedUser(
                                service.getServiceManagementId(),
                                service.getName(),
                                service.getDepartment().getName())
                )
                .toList();
    }

    public ResponseGetServiceByIdDto getServiceManagementById(String serviceManagementId) {

        var service = this.serviceRepository.findByServiceManagementId(serviceManagementId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        var updateAt = "";

        if (service.getUpdatedAt() != null ) {
            updateAt = service.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        return new ResponseGetServiceByIdDto(
                service.getServiceManagementId(),
                service.getName(),
                service.getCode(),
                service.getDescription(),
                service.getDepartment().getDepartmentId(),
                service.getDepartment().getName(),
                service.getActive(),
                service.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );
    }

    @Transactional
    public ResponseServiceManagementDto deleteServiceManagement(String serviceManagementId) {

        var service = this.serviceRepository.findByServiceManagementId(serviceManagementId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        var response = new ResponseServiceManagementDto(
                service.getServiceManagementId(),
                service.getName(),
                service.getCode(),
                service.getDescription(),
                service.getDepartment().getDepartmentId(),
                service.getDepartment().getName(),
                service.getActive()
        );

        // Remove os vínculos dos usuários com esse serviço
        this.serviceRepository.deleteUserServicesByServiceId(service.getServiceManagementId());

        // Agora remove o serviço
        this.serviceRepository.delete(service);

        return response;
    }

    public ResponseServiceDashBoardDto getStatistics() {

        var countTotalServices = this.serviceRepository.countTotalServicesStatisticsDto();
        var getPercentagesByServices = this.serviceRepository.getServicePercentagesStatisticsDto();

        return new ResponseServiceDashBoardDto(
                countTotalServices,
                getPercentagesByServices
        );
    }
}