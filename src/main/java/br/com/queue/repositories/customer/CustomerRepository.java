package br.com.queue.repositories.customer;

import br.com.queue.dtos.customer.allCustomer.ResponseAllCustomersDto;
import br.com.queue.entities.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByCustomerId(String customerId);

    @Modifying
    void deleteByCustomerId(String customerId);

    @Query(value = """
        SELECT
            c.customer_id AS customerId,
            c.name AS name,
            c.cpf AS cpf,
            c.rg AS rg,
            c.phone AS phone,
            c.email AS email
        FROM tb_customers c
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(c.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.cpf))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.rg))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.phone))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.email))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        ORDER BY COALESCE(c.updated_at, c.created_at) DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM tb_customers c
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(c.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.cpf))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.rg))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.phone))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(c.email))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        """,
            nativeQuery = true
    )
    Page<ResponseAllCustomersDto> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
