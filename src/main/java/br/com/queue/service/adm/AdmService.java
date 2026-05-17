package br.com.queue.service.adm;

import br.com.queue.entities.user.User;
import br.com.queue.enums.Role;
import br.com.queue.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

@RequiredArgsConstructor
public class AdmService implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        var user = this.userRepository.findByUserId("123");

        user.ifPresentOrElse(
                present -> System.out.println("ADM On"),
                () -> {
                    var newEntity = new User();
                    newEntity.setRole(Role.ADMIN);
                    newEntity.setEmail("admin");
                    newEntity.setPassword("admin");
                }
        );
    }
}
