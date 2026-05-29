package br.com.queue.controller.serviceManagement;

import br.com.queue.dtos.serviceManagement.create.CreateServiceManagementDto;
import br.com.queue.dtos.serviceManagement.ResponseServiceManagementDto;
import br.com.queue.dtos.serviceManagement.getServiceDto.ResponseGetServiceByIdDto;
import br.com.queue.dtos.serviceManagement.statistics.ResponseStatisticsDto;
import br.com.queue.dtos.serviceManagement.update.UpdateServiceManagementDto;
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

    @PatchMapping
    public ResponseEntity<ResponseServiceManagementDto> updateServiceManagement(
            @RequestBody UpdateServiceManagementDto dto
    ) {

        var response = this.serviceManagementService.updateServiceManagement(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseServiceManagementDto>> getAllServicesManagement(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search
    ) {

        var response = this.serviceManagementService.getAllServicesManagement(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceManagementId}")
    public ResponseEntity<ResponseGetServiceByIdDto> getServiceManagementById(
            @PathVariable String serviceManagementId
    ) {

        var response = this.serviceManagementService.getServiceManagementById(serviceManagementId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{serviceManagementId}")
    public ResponseEntity<ResponseServiceManagementDto> deleteServiceManagement(@PathVariable String serviceManagementId) {

        var response = this.serviceManagementService.deleteServiceManagement(serviceManagementId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ResponseStatisticsDto> getStatistics() {

        var response = this.serviceManagementService.getStatistics();
        return ResponseEntity.ok(response);
    }
}