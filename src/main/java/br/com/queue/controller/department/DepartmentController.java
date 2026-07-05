package br.com.queue.controller.department;

import br.com.queue.dtos.department.ResponseDepartmentDto;
import br.com.queue.dtos.department.create.CreateDepartmentDto;
import br.com.queue.dtos.department.getDepartment.ResponseDepartmentNamesDto;
import br.com.queue.dtos.department.getDepartment.ResponseGetDepartment;
import br.com.queue.dtos.department.statistics.ResponseDepartmentDashBoardDto;
import br.com.queue.dtos.department.update.UpdateDepartmentDto;
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
    public ResponseEntity<ResponseDepartmentDto> updateDepartment(@RequestBody UpdateDepartmentDto dto) {

        var response = this.departmentService.updateDepartment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseDepartmentDto>> getAllDepartments(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search
    ) {

        var response = this.departmentService.getAllDepartments(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/names")
    public ResponseEntity<List<ResponseDepartmentNamesDto>> getAllDepartmentNames() {

        var response = this.departmentService.getDepartmentNames();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ResponseGetDepartment> getDepartmentById(@PathVariable String departmentId) {

        var response = this.departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ResponseDepartmentDto> deleteDepartment(@PathVariable String departmentId) {

        var response = this.departmentService.deleteDepartment(departmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ResponseDepartmentDashBoardDto> getStatistics() {

        var response = this.departmentService.getStatistics();
        return ResponseEntity.ok(response);
    }
}