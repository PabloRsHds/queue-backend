package br.com.queue.repositories.user;

import br.com.queue.dtos.user.ResponseUserDto;
import br.com.queue.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    @Modifying
    void deleteByUserId(String userId);

    @Query(value = """
        SELECT
            u.user_id AS userId,
            u.username AS username,
            u.name AS name,
            u.surname AS surname,
            u.email AS email,
            u.role AS role,
            u.counter_number AS counterNumber,
            u.active AS active,
            u.created_at AS createdAt,
            u.updated_at AS updatedAt
        FROM tb_users u
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(u.username))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.surname))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.email))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.role))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        ORDER BY COALESCE(u.updated_at, u.created_at) DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM tb_users u
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(u.username))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.surname))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.email))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(u.role))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        """,
            nativeQuery = true
    )
    Page<ResponseUserDto> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
