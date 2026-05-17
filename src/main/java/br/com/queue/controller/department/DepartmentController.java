package br.com.queue.controller.department;

import br.com.queue.dto.department.allDepartment.ResponseAllDepartmentDto;
import br.com.queue.dto.department.create.CreateDepartmentDto;
import br.com.queue.dto.department.create.ResponseDepartmentDto;
import br.com.queue.dto.department.getDepartment.ResponseGetDepartment;
import br.com.queue.dto.department.update.ResponseUpdateDepartmentDto;
import br.com.queue.dto.department.update.UpdateDepartmentDto;
import br.com.queue.service.department.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ResponseDepartmentDto> createDepartment(@RequestBody @Valid CreateDepartmentDto dto) {

        var response = this.departmentService.createDepartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping
    public ResponseEntity<ResponseUpdateDepartmentDto> updateDepartment(@RequestBody UpdateDepartmentDto dto) {

        var response = this.departmentService.updateDepartment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllDepartmentDto>> getAllDepartments(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search
    ) {

        var response = this.departmentService.getAllDepartments(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ResponseGetDepartment> getDepartmentById(@PathVariable String departmentId) {

        var response = this.departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String departmentId) {

        this.departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }
}