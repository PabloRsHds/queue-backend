package br.com.queue.repositories.user;

import br.com.queue.dtos.statistics.ResponseUserStatisticsDto;
import br.com.queue.dtos.user.users.ResponseAllUsersDto;
import br.com.queue.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface  UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    @Modifying
    void deleteByUserId(String userId);

    @Query(value = """
    SELECT
        u.user_id AS userId,
        u.username AS username,
        u.email AS email,
        u.role::TEXT AS role,
        u.active AS active
    FROM tb_users u
    WHERE (
        :search IS NULL
        OR :search = ''
        OR unaccent(LOWER(u.username))
            LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        OR unaccent(LOWER(u.email))
            LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        OR unaccent(LOWER(u.role::TEXT))
            LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        OR LOWER(u.active::TEXT)
            LIKE LOWER(CONCAT('%', :search, '%'))
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
        OR unaccent(LOWER(u.email))
            LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        OR unaccent(LOWER(u.role::TEXT))
            LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        OR LOWER(u.active::TEXT)
            LIKE LOWER(CONCAT('%', :search, '%'))
    )
    """,
            nativeQuery = true
    )
    Page<ResponseAllUsersDto> findAllWithSearch(@Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT
                COUNT(*) FILTER (WHERE role = 'ADMIN') AS admins,
                COUNT(*) AS totalElements,
                COUNT(*) FILTER (WHERE active = true) AS totalElementsActive,
                COUNT(*) FILTER (WHERE active = false) AS totalElementsInactive,
                ROUND(
                    (
                        COUNT(*) FILTER (WHERE active = true)::numeric
                        / NULLIF(COUNT(*), 0)
                    ) * 100,
                    2
                ) AS percentageActive,
                ROUND(
                    (
                        COUNT(*) FILTER (WHERE active = false)::numeric
                        / NULLIF(COUNT(*), 0)
                    ) * 100,
                    2
                ) AS percentageInactive
            FROM tb_users
            """,
            nativeQuery = true)
    ResponseUserStatisticsDto getStatistics();
}
