package br.com.queue.service.unit;

import br.com.queue.dtos.unit.CreateUnitDto;
import br.com.queue.dtos.unit.ResponseUnitDto;
import br.com.queue.dtos.unit.UpdateUnitDto;
import br.com.queue.entities.unit.Unit;
import br.com.queue.repositories.unit.UnitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public Page<ResponseUnitDto> getAllUnits(int page, int size, String search) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return unitRepository.findAllWithSearch(normalizedSearch, PageRequest.of(page, size));
    }

    @Transactional
    public ResponseUnitDto createUnit(CreateUnitDto dto) {

        var entity = new Unit();

        entity.setName(dto.name());
        entity.setAddress(dto.address());
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());

        unitRepository.save(entity);

        return toDto(entity);
    }

    @Transactional
    public ResponseUnitDto updateUnit(UpdateUnitDto dto) {

        var entity = unitRepository.findById(dto.unitId())
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada."));

        if (dto.name() != null && !dto.name().isBlank()) {
            entity.setName(dto.name());
        }

        if (dto.address() != null) {
            entity.setAddress(dto.address());
        }

        if (dto.active() != null) {
            entity.setActive(dto.active());
        }

        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        unitRepository.save(entity);

        return toDto(entity);
    }

    @Transactional
    public ResponseUnitDto deleteUnit(String unitId) {

        var entity = unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada."));

        var response = this.toDto(entity);
        unitRepository.delete(entity);

        return response;
    }

    @Transactional(readOnly = true)
    public ResponseUnitDto getUnitById(String unitId) {

        var unit = this.unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));

        var updatedAt = "";

        if (unit.getUpdatedAt() != null) {
            updatedAt = unit.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updatedAt = null;
        }

        return new ResponseUnitDto(
                unit.getUnitId(),
                unit.getName(),
                unit.getAddress(),
                unit.getActive(),
                unit.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updatedAt
        );
    }

    private ResponseUnitDto toDto(Unit entity) {

        return new ResponseUnitDto(
                entity.getUnitId(),
                entity.getName(),
                entity.getAddress(),
                entity.getActive(),
                entity.getCreatedAt() != null
                        ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : null,
                entity.getUpdatedAt() != null
                        ? entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : null
        );
    }
}
