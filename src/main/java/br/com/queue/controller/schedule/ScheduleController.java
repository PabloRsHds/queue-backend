package br.com.queue.controller.schedule;

import br.com.queue.dtos.schedule.allSchedules.ResponseAllSchedulesDto;
import br.com.queue.dtos.schedule.create.CreateScheduleDto;
import br.com.queue.dtos.schedule.create.ResponseScheduleDto;
import br.com.queue.dtos.schedule.update.ResponseUpdateScheduleDto;
import br.com.queue.dtos.schedule.update.UpdateScheduleDto;
import br.com.queue.service.schedule.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/scheduling")
@RequiredArgsConstructor
public class ScheduleController {

    private final SchedulingService schedulingService;

    @PostMapping
    public ResponseEntity<ResponseScheduleDto> createSchedule(@RequestBody CreateScheduleDto dto) {

        var response = this.schedulingService.createSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<ResponseUpdateScheduleDto> updateSchedule(@RequestBody UpdateScheduleDto dto) {

        var response = this.schedulingService.updateSchedule(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllSchedulesDto>> getAllSchedules(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) LocalDate scheduleDate,
            @RequestParam(required = false) String search
    ) {

        var response = this.schedulingService.getAllSchedules(page, size, search, scheduleDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ResponseScheduleDto> getScheduleById(@PathVariable String scheduleId) {

        var response = this.schedulingService.getScheduleById(scheduleId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String scheduleId) {

        this.schedulingService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}