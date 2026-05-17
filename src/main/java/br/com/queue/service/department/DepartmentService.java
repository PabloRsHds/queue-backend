package br.com.queue.service.department;

import br.com.queue.dto.department.allDepartment.ResponseAllDepartmentDto;
import br.com.queue.dto.department.create.CreateDepartmentDto;
import br.com.queue.dto.department.create.ResponseDepartmentDto;
import br.com.queue.dto.department.getDepartment.ResponseGetDepartment;
import br.com.queue.dto.department.update.ResponseUpdateDepartmentDto;
import br.com.queue.dto.department.update.UpdateDepartmentDto;
import br.com.queue.entities.department.Department;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.repositories.department.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public ResponseDepartmentDto createDepartment(CreateDepartmentDto dto) {

        var entity = new Department();

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        this.departmentRepository.save(entity);

        return new ResponseDepartmentDto(
                entity.getDepartmentId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    @Transactional
    public ResponseUpdateDepartmentDto updateDepartment(UpdateDepartmentDto dto) {

        Department department = this.departmentRepository.findByDepartmentId(dto.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        if (!dto.name().isBlank()) {
            department.setName(dto.name());
        }

        if (!dto.description().isBlank()) {
            department.setDescription(dto.description());
        }

        department.setDescription(dto.description());

        this.departmentRepository.save(department);

        return new ResponseUpdateDepartmentDto(
                department.getName(),
                department.getDescription()
        );
    }

    public Page<ResponseAllDepartmentDto> getAllDepartments(int page, int size, String search) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        Page<Department> departments = this.departmentRepository
                .findAllWithSearch(normalizedSearch, PageRequest.of(page, size));

        return departments.map(department -> new ResponseAllDepartmentDto(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription()
        ));
    }

    public ResponseGetDepartment getDepartmentById(String departmentId) {

        Department department = this.departmentRepository.findByDepartmentId(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        var services = department.getServices().stream().map(ServiceManagement::getName).toList();

        return new ResponseGetDepartment(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription(),
                department.getActive(),
                department.getCreatedAt(),
                department.getUpdatedAt(),
                services
        );
    }

    @Transactional
    public void deleteDepartment(String departmentId) {

        Department department = this.departmentRepository.findByDepartmentId(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        this.departmentRepository.delete(department);
    }
}