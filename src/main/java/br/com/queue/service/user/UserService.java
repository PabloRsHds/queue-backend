package br.com.queue.service.user;

import br.com.queue.dto.user.allUsers.ResponseAllUsersDto;
import br.com.queue.dto.user.create.CreateUserDto;
import br.com.queue.dto.user.create.ResponseUserDto;
import br.com.queue.dto.user.update.ResponseUpdateUserDto;
import br.com.queue.dto.user.update.UpdateUserDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.entities.user.User;
import br.com.queue.enums.Role;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import br.com.queue.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ServiceManagementRepository serviceManagementRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseUserDto createUser(CreateUserDto dto) {

        Set<ServiceManagement> services = new HashSet<>(
                this.serviceManagementRepository.findAllByServiceManagementIdIn(dto.serviceIds())
        );

        var entity = new User();
        entity.setUsername(dto.username());
        entity.setName(dto.name());
        entity.setSurname(dto.surname());
        entity.setEmail(dto.email());
        entity.setPassword(passwordEncoder.encode(dto.password()));
        entity.setRole(Role.valueOf(dto.role()));
        entity.setCounterNumber(dto.counterNumber());
        entity.setActive(true);
        entity.setServices(services);

        this.userRepository.save(entity);

        return new ResponseUserDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive()
        );
    }

    @Transactional
    public ResponseUpdateUserDto updateUser(UpdateUserDto dto) {

        var user = this.userRepository.findByUserId(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Set<ServiceManagement> services = new HashSet<>(
                this.serviceManagementRepository.findAllByServiceManagementIdIn(dto.serviceIds())
        );

        user.setUsername(dto.username());
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setEmail(dto.email());
        user.setRole(Role.valueOf(dto.role()));
        user.setCounterNumber(dto.counterNumber());
        user.setActive(dto.active());
        user.setServices(services);

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        this.userRepository.save(user);

        return new ResponseUpdateUserDto(
                user.getUserId(),
                user.getUsername(),
                user.getSurname(),
                user.getEmail(),
                user.getRole().name(),
                user.getCounterNumber(),
                user.getActive()
        );
    }

    public Page<ResponseAllUsersDto> getAllUsers(int page, int size) {

        return this.userRepository.findAll(PageRequest.of(page, size))
                .map(user -> new ResponseAllUsersDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getCounterNumber(),
                        user.getActive()
                ));
    }

    public ResponseUserDto getUserById(String userId) {

        var user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return new ResponseUserDto(
                user.getUserId(),
                user.getUsername(),
                user.getSurname(),
                user.getEmail(),
                user.getRole().name(),
                user.getCounterNumber(),
                user.getActive()
        );
    }

    @Transactional
    public void deleteUser(String userId) {

        var user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        this.userRepository.delete(user);
    }
}