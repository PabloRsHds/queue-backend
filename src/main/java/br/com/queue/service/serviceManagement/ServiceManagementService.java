package br.com.queue.service.serviceManagement;

import br.com.queue.dto.serviceManagement.allServicesManagement.ResponseAllServicesManagementDto;
import br.com.queue.dto.serviceManagement.create.CreateServiceManagementDto;
import br.com.queue.dto.serviceManagement.create.ResponseServiceManagementDto;
import br.com.queue.dto.serviceManagement.update.ResponseUpdateServiceManagementDto;
import br.com.queue.dto.serviceManagement.update.UpdateServiceManagementDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.repositories.department.DepartmentRepository;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    public ResponseUpdateServiceManagementDto updateServiceManagement(UpdateServiceManagementDto dto) {

        var service = this.serviceRepository.findByServiceManagementId(dto.serviceManagementId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        var department = this.departmentRepository.findByDepartmentId(dto.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        service.setName(dto.name());
        service.setCode(dto.code());
        service.setDescription(dto.description());
        service.setDepartment(department);
        service.setActive(dto.active());

        this.serviceRepository.save(service);

        return new ResponseUpdateServiceManagementDto(
                service.getServiceManagementId(),
                service.getName(),
                service.getCode(),
                service.getDescription(),
                service.getDepartment().getDepartmentId(),
                service.getDepartment().getName(),
                service.getActive()
        );
    }

    public Page<ResponseAllServicesManagementDto> getAllServicesManagement(int page, int size) {

        return this.serviceRepository.findAll(PageRequest.of(page, size))
                .map(service -> new ResponseAllServicesManagementDto(
                        service.getServiceManagementId(),
                        service.getName(),
                        service.getCode(),
                        service.getDepartment().getName(),
                        service.getActive()
                ));
    }

    public ResponseServiceManagementDto getServiceManagementById(String serviceManagementId) {

        var service = this.serviceRepository.findByServiceManagementId(serviceManagementId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

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

    @Transactional
    public void deleteServiceManagement(String serviceManagementId) {

        var service = this.serviceRepository.findByServiceManagementId(serviceManagementId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado"));

        this.serviceRepository.delete(service);
    }
}