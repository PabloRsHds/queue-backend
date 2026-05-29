package br.com.queue.controller.customer;

import br.com.queue.dtos.customer.allCustomer.ResponseAllCustomersDto;
import br.com.queue.dtos.customer.create.CreateCustomerDto;
import br.com.queue.dtos.customer.create.ResponseCustomerDto;
import br.com.queue.dtos.customer.update.ResponseUpdateCustomerDto;
import br.com.queue.dtos.customer.update.UpdateCustomerDto;
import br.com.queue.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ResponseCustomerDto> createCustomer(@RequestBody CreateCustomerDto dto) {

        var response = this.customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ResponseUpdateCustomerDto> updateCustomer(@RequestBody UpdateCustomerDto dto) {

        var response = this.customerService.updateCustomer(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseAllCustomersDto>> getAllCustomers(
            @RequestParam int page,
            @RequestParam int size
    ) {

        var response = this.customerService.getAllCustomers(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseCustomerDto> getCustomerById(@PathVariable String customerId) {

        var response = this.customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {

        this.customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
