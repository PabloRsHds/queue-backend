package br.com.queue.controller.ticket;

import br.com.queue.dtos.ticket.allTickets.ResponseAllTicketsDto;
import br.com.queue.dtos.ticket.callTicket.CallTicketDto;
import br.com.queue.dtos.ticket.callTicket.ResponseCallTicketDto;
import br.com.queue.dtos.ticket.create.CreateTicketDto;
import br.com.queue.dtos.ticket.create.ResponseTicketDto;
import br.com.queue.dtos.ticket.finishTicket.FinishTicketDto;
import br.com.queue.dtos.ticket.finishTicket.ResponseFinishTicketDto;
import br.com.queue.dtos.ticket.startAttendance.ResponseStartAttendanceDto;
import br.com.queue.dtos.ticket.startAttendance.StartAttendanceDto;
import br.com.queue.service.ticket.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<ResponseTicketDto> createTicket(
            @RequestBody CreateTicketDto dto
    ) {

        var response = this.ticketService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/call")
    public ResponseEntity<ResponseCallTicketDto> callTicket(
            @RequestBody CallTicketDto dto
    ) {

        var response = this.ticketService.callTicket(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/start")
    public ResponseEntity<ResponseStartAttendanceDto> startAttendance(
            @RequestBody StartAttendanceDto dto
    ) {

        var response = this.ticketService.startAttendance(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/finish")
    public ResponseEntity<ResponseFinishTicketDto> finishTicket(
            @RequestBody FinishTicketDto dto
    ) {

        var response = this.ticketService.finishTicket(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllTicketsDto>> getAllTickets(
            @RequestParam int page,
            @RequestParam int size
    ) {

        var response = this.ticketService.getAllTickets(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ResponseTicketDto> getTicketById(
            @PathVariable String ticketId
    ) {

        var response = this.ticketService.getTicketById(ticketId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String ticketId) {

        this.ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}