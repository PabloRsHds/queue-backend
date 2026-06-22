package br.com.queue.controller.attendance;

import br.com.queue.dtos.attendance.allAttendances.ResponseAllAttendances;
import br.com.queue.dtos.attendance.start.StartAttendanceDto;
import br.com.queue.dtos.attendance.start.FinishAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseAttendanceDto;
import br.com.queue.dtos.attendance.finish.ResponseFinishAttendanceDto;
import br.com.queue.dtos.attendance.statistics.ResponseAttendanceStatisticsDto;
import br.com.queue.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<ResponseAttendanceDto> startAttendance(@RequestBody StartAttendanceDto dto) {

        var response = this.attendanceService.startAttendance(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/finish")
    public ResponseEntity<ResponseFinishAttendanceDto> finishAttendance(@RequestBody FinishAttendanceDto dto) {

        var response = this.attendanceService.finishAttendance(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllAttendances>> getAllAttendances(@RequestParam int page, @RequestParam int size) {

        var response = attendanceService.getAllAttendances(page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{attendanceId}")
    public void deleteAttendance(@PathVariable String attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ResponseAttendanceStatisticsDto> getAttendanceStatistics() {
        var response = this.attendanceService.getAttendanceStatistics();
        return ResponseEntity.ok(response);
    }
}