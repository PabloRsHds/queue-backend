package br.com.queue.repositories.user;

import br.com.queue.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    @Modifying
    void deleteByUserId(String userId);
}
