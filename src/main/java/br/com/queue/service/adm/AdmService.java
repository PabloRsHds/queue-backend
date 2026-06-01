package br.com.queue.service.adm;

import br.com.queue.entities.user.User;
import br.com.queue.enums.Role;
import br.com.queue.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdmService implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        var user = this.userRepository.findByEmail("admin@gmail.com");

        user.ifPresentOrElse(
                present -> System.out.println("ADM On"),
                () -> {
                    var adm1 = new User();

                    adm1.setUsername("pablorsh");
                    adm1.setName("Pablo");
                    adm1.setSurname("Renato");
                    adm1.setActive(true);
                    adm1.setCreatedAt(LocalDateTime.now());
                    adm1.setRole(Role.ADMIN);
                    adm1.setEmail("admin@gmail.com");
                    adm1.setPassword("admin");

                    this.userRepository.save(adm1);
                }
        );
    }
}
