package br.com.queue.service.department;

import br.com.queue.dtos.department.ResponseDepartmentDto;
import br.com.queue.dtos.department.create.CreateDepartmentDto;
import br.com.queue.dtos.department.getDepartment.ResponseDepartmentNamesDto;
import br.com.queue.dtos.department.getDepartment.ResponseGetDepartment;
import br.com.queue.dtos.department.statistics.ResponseDepartmentDashBoardDto;
import br.com.queue.dtos.department.update.UpdateDepartmentDto;
import br.com.queue.dtos.statistics.ResponseStatisticsDto;
import br.com.queue.entities.department.Department;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.repositories.department.DepartmentRepository;
import br.com.queue.repositories.unit.UnitRepository;
import br.com.queue.service.unit.UnitContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UnitContext unitContext;

    @Transactional
    public ResponseDepartmentDto createDepartment(JwtAuthenticationToken token, CreateDepartmentDto dto) {

        var unit = this.unitContext.getCurrentUnit(token);

        var entity = new Department();

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setActive(true);
        entity.setUnit(unit);

        this.departmentRepository.save(entity);

        return new ResponseDepartmentDto(
                entity.getDepartmentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive()
        );
    }

    @Transactional
    public ResponseDepartmentDto updateDepartment(UpdateDepartmentDto dto) {

        var entity = this.departmentRepository.findByDepartmentId(dto.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        if (!dto.name().isBlank()) {
            entity.setName(dto.name());
        }

        entity.setDescription(dto.description());
        entity.setActive(dto.active());
        entity.setUpdatedAt(LocalDateTime.now());
        this.departmentRepository.save(entity);

        return new ResponseDepartmentDto(
                entity.getDepartmentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive()
        );
    }

    public Page<ResponseDepartmentDto> getAllDepartments(
            JwtAuthenticationToken token,
            int page,
            int size,
            String search) {

        var unit = this.unitContext.getCurrentUnit(token);

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.departmentRepository.findAllWithSearch(
                unit.getUnitId(),
                normalizedSearch,
                PageRequest.of(page, size)
        );
    }

    public List<ResponseDepartmentNamesDto> getDepartmentNames(){

        return this.departmentRepository.findAll()
                .stream()
                .map(department -> new ResponseDepartmentNamesDto(department.getName()))
                .toList();
    }

    public ResponseGetDepartment getDepartmentById(String departmentId) {

        var department = this.departmentRepository.findByDepartmentId(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        var services = department.getServices().stream().map(ServiceManagement::getName).toList();

        var updatedAt = "";

        if (department.getUpdatedAt() != null) {
            updatedAt = department.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updatedAt = null;
        }

        return new ResponseGetDepartment(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription(),
                department.getActive(),
                department.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updatedAt,
                services
        );
    }

    @Transactional
    public ResponseDepartmentDto deleteDepartment(String departmentId) {

        var entity = this.departmentRepository.findByDepartmentId(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        var response = new ResponseDepartmentDto(
                entity.getDepartmentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive()
        );

        this.departmentRepository.delete(entity);

        return response;
    }

    public ResponseDepartmentDashBoardDto getStatistics(JwtAuthenticationToken token) {

        var unit = this.unitContext.getCurrentUnit(token);

        var totalDepartment = this.departmentRepository.countTotalDepartmentsStatisticsDto(unit.getUnitId());
        var countServicesByDepartments = this.departmentRepository.countServicesByDepartmentStatisticsDto(unit.getUnitId());
        var departmentPercentages = this.departmentRepository.getDepartmentPercentagesStatisticsDto(unit.getUnitId());
        var departmentsCreatedByMonth = this.departmentRepository.countDepartmentsCreatedByMonth(unit.getUnitId());

        return new ResponseDepartmentDashBoardDto(
                totalDepartment,
                countServicesByDepartments,
                departmentPercentages,
                departmentsCreatedByMonth
        );
    }
}