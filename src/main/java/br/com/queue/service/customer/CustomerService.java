package br.com.queue.service.customer;

import br.com.queue.dtos.customer.allCustomer.ResponseAllCustomersDto;
import br.com.queue.dtos.customer.create.CreateCustomerDto;
import br.com.queue.dtos.customer.create.ResponseCustomerDto;
import br.com.queue.dtos.customer.update.ResponseUpdateCustomerDto;
import br.com.queue.dtos.customer.update.UpdateCustomerDto;
import br.com.queue.entities.customer.Customer;
import br.com.queue.repositories.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public ResponseCustomerDto createCustomer(CreateCustomerDto dto) {

        var entity = new Customer();

        entity.setName(dto.name());
        entity.setCpf(dto.cpf());
        entity.setRg(dto.rg());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setCreatedAt(LocalDateTime.now());

        this.customerRepository.save(entity);

        return new ResponseCustomerDto(
                entity.getName(),
                entity.getCpf(),
                entity.getRg(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }

    @Transactional
    public ResponseUpdateCustomerDto updateCustomer(UpdateCustomerDto dto) {

        Customer customer = this.customerRepository.findByCustomerId(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        customer.setName(dto.name());
        customer.setCpf(dto.cpf());
        customer.setRg(dto.rg());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());
        customer.setUpdatedAt(LocalDateTime.now());

        this.customerRepository.save(customer);

        return new ResponseUpdateCustomerDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getCpf(),
                customer.getRg(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getUpdatedAt()
        );
    }

    public Page<ResponseAllCustomersDto> getAllCustomers(int page, int size) {

        return this.customerRepository.findAll(PageRequest.of(page, size))
                .map(customer -> new ResponseAllCustomersDto(
                        customer.getName(),
                        customer.getCpf(),
                        customer.getRg(),
                        customer.getPhone(),
                        customer.getEmail(),
                        customer.getCreatedAt()
                ));
    }

    public ResponseCustomerDto getCustomerById(String customerId) {

        Customer customer = this.customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        return new ResponseCustomerDto(
                customer.getName(),
                customer.getCpf(),
                customer.getRg(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }

    @Transactional
    public void deleteCustomer(String customerId) {

        Customer customer = this.customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        this.customerRepository.delete(customer);
    }
}
