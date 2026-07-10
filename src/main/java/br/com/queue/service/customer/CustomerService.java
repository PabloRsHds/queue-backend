package br.com.queue.service.customer;

import br.com.queue.dtos.customer.allCustomer.ResponseAllCustomersDto;
import br.com.queue.dtos.customer.create.CreateCustomerDto;
import br.com.queue.dtos.customer.create.ResponseCustomerDto;
import br.com.queue.dtos.customer.getCustomer.ResponseCustomerById;
import br.com.queue.dtos.customer.getCustomer.ResponseGetCustomerIdsAndNames;
import br.com.queue.dtos.customer.statistics.ResponseCustomerDashBoardDto;
import br.com.queue.dtos.customer.update.UpdateCustomerDto;
import br.com.queue.dtos.user.metrics.ResponseUserDashBoardDto;
import br.com.queue.entities.customer.Customer;
import br.com.queue.entities.ticket.Ticket;
import br.com.queue.repositories.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public ResponseCustomerDto registerCustomer(CreateCustomerDto dto) {

        var entity = new Customer();

        entity.setName(dto.name());
        entity.setCpf(dto.cpf());
        entity.setRg(dto.rg());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setCreatedAt(LocalDateTime.now());

        this.customerRepository.save(entity);

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        return new ResponseCustomerDto(
                entity.getCustomerId(),
                entity.getName(),
                entity.getCpf(),
                entity.getRg(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );
    }

    @Transactional
    public ResponseCustomerDto updateCustomer(UpdateCustomerDto dto) {

        Customer customer = this.customerRepository.findByCustomerId(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        customer.setName(dto.name());
        customer.setCpf(dto.cpf());
        customer.setRg(dto.rg());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());
        customer.setUpdatedAt(LocalDateTime.now());

        this.customerRepository.save(customer);

        return new ResponseCustomerDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getCpf(),
                customer.getRg(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                customer.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    public Page<ResponseAllCustomersDto> getAllCustomers(int page, int size, String search) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.customerRepository.findAllWithSearch(normalizedSearch,
                PageRequest.of(page, size));
    }

    public List<ResponseGetCustomerIdsAndNames> getCustomerIdsAndNames() {

        return this.customerRepository.findAll()
                .stream()
                .map(customer ->
                        new ResponseGetCustomerIdsAndNames(customer.getCustomerId(), customer.getName()))
                .toList();
    }

    public ResponseCustomerById getCustomerById(String customerId) {

        var customer = this.customerRepository.findByCustomerId(customerId)
                .orElseThrow();

        String updateAt = customer.getUpdatedAt() != null
                ? customer.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;

        String ticketCode = customer.getTickets()
                .stream()
                .findFirst()
                .map(Ticket::getCode)
                .filter(code -> !code.isBlank())
                .orElse(null);

        return new ResponseCustomerById(
                customer.getCustomerId(),
                customer.getName(),
                customer.getCpf(),
                customer.getRg(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt,
                ticketCode
        );
    }

    @Transactional
    public ResponseCustomerDto deleteCustomer(String customerId) {

        var entity = this.customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        var response = new ResponseCustomerDto(
                entity.getCustomerId(),
                entity.getName(),
                entity.getCpf(),
                entity.getRg(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );

        this.customerRepository.delete(entity);
        return response;
    }

    public ResponseCustomerDashBoardDto getStatistics() {

        var countTotalCustomersStatistics = this.customerRepository.countTotalCustomerStatisticsDto();
        var customersCreatedByMonthStatistics = this.customerRepository.countCustomersCreatedByMonth();

        return new ResponseCustomerDashBoardDto(
                countTotalCustomersStatistics,
                customersCreatedByMonthStatistics
        );
    }
}
