package br.com.queue.repositories.ticket;

import br.com.queue.entities.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    Optional<Ticket> findByTicketId(String ticketId);

    Optional<Ticket> findTicketByScheduleScheduleId(String scheduleId);

    // Criar a sequência no database
    // CREATE SEQUENCE ticket_call_number_seq
    // START 1;
    @Query(value = "SELECT nextval('ticket_call_number_seq')", nativeQuery = true)
    Long getNextCallNumber();
}
