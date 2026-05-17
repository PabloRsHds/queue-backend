package br.com.queue.controller.serviceManagement;

import br.com.queue.dto.serviceManagement.allServicesManagement.ResponseAllServicesManagementDto;
import br.com.queue.dto.serviceManagement.create.CreateServiceManagementDto;
import br.com.queue.dto.serviceManagement.create.ResponseServiceManagementDto;
import br.com.queue.dto.serviceManagement.update.ResponseUpdateServiceManagementDto;
import br.com.queue.dto.serviceManagement.update.UpdateServiceManagementDto;
import br.com.queue.service.serviceManagement.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceManagementController {

    private final ServiceManagementService serviceManagementService;

    @PostMapping
    public ResponseEntity<ResponseServiceManagementDto> createServiceManagement(
            @RequestBody CreateServiceManagementDto dto
    ) {

        var response = this.serviceManagementService.createServiceManagement(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ResponseUpdateServiceManagementDto> updateServiceManagement(
            @RequestBody UpdateServiceManagementDto dto
    ) {

        var response = this.serviceManagementService.updateServiceManagement(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllServicesManagementDto>> getAllServicesManagement(
            @RequestParam int page,
            @RequestParam int size
    ) {

        var response = this.serviceManagementService.getAllServicesManagement(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceManagementId}")
    public ResponseEntity<ResponseServiceManagementDto> getServiceManagementById(
            @PathVariable String serviceManagementId
    ) {

        var response = this.serviceManagementService.getServiceManagementById(serviceManagementId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{serviceManagementId}")
    public ResponseEntity<Void> deleteServiceManagement(@PathVariable String serviceManagementId) {

        this.serviceManagementService.deleteServiceManagement(serviceManagementId);

        return ResponseEntity.noContent().build();
    }
}