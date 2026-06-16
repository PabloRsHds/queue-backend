package br.com.queue.repositories.ticket;

import br.com.queue.entities.customer.Customer;
import br.com.queue.entities.schedule.Schedule;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.entities.ticket.Ticket;
import br.com.queue.enums.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    Optional<Ticket> findByTicketId(String ticketId);

    Optional<Ticket> findByCustomerAndServiceManagementAndScheduleAndPriority(
            Customer customer,
            ServiceManagement serviceManagement,
            Schedule schedule,
            PriorityLevel priority
    );
}
