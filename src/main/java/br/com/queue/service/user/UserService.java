package br.com.queue.service.user;

import br.com.queue.dtos.statistics.ResponseUserStatisticsDto;
import br.com.queue.dtos.user.ResponseUserDto;
import br.com.queue.dtos.user.create.CreateUserDto;
import br.com.queue.dtos.user.get_user.ResponseUserInfoDto;
import br.com.queue.dtos.user.update.UpdateUserDto;
import br.com.queue.dtos.user.users.ResponseAllUsersDto;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ServiceManagementRepository serviceManagementRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseUserDto createUser(CreateUserDto dto) {

        Set<ServiceManagement> services =
                serviceManagementRepository.findAllByServiceManagementIdIn(dto.serviceIds());

        var entity = new User();
        entity.setUsername(dto.username());
        entity.setName(dto.name());
        entity.setSurname(dto.surname());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setPassword(passwordEncoder.encode(dto.password()));
        entity.setRole(Role.valueOf(dto.role()));
        entity.setCounterNumber(dto.counterNumber());
        entity.setActive(true);
        entity.setServices(services);
        entity.setCreatedAt(LocalDateTime.now());

        this.userRepository.save(entity);

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        return new ResponseUserDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getName(),
                entity.getSurname(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );
    }

    @Transactional
    public ResponseUserDto updateUser(UpdateUserDto dto) {

        var entity = this.userRepository.findByUserId(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Set<ServiceManagement> services = new HashSet<>(
                this.serviceManagementRepository.findAllByServiceManagementIdIn(dto.serviceIds())
        );

        entity.setUsername(dto.username());
        entity.setName(dto.name());
        entity.setSurname(dto.surname());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setRole(Role.valueOf(dto.role()));
        entity.setActive(dto.active());
        entity.setServices(services);
        entity.setUpdatedAt(LocalDateTime.now());

        if (!dto.role().equals("ATTENDANT")) {
            entity.setCounterNumber(null);
        } else {
            entity.setCounterNumber(dto.counterNumber());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.password()));
        }

        this.userRepository.save(entity);

        return new ResponseUserDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getName(),
                entity.getSurname(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    public Page<ResponseAllUsersDto> getAllUsers(int page, int size, String search) {

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.userRepository.findAllWithSearch(normalizedSearch,
                PageRequest.of(page, size));
    }

    public ResponseUserInfoDto getUserById(String userId) {

        var entity = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        return new ResponseUserInfoDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getName(),
                entity.getSurname(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt,
                entity.getServices()
                        .stream()
                        .map(ServiceManagement::getName)
                        .collect(Collectors.toSet())
        );
    }

    public ResponseUserInfoDto getUserByToken(JwtAuthenticationToken token) {

        var entity = this.userRepository.findByUserId(token.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        return new ResponseUserInfoDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getName(),
                entity.getSurname(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt,
                entity.getServices()
                        .stream()
                        .map(ServiceManagement::getName)
                        .collect(Collectors.toSet())
        );
    }

    @Transactional
    public ResponseUserDto deleteUser(String userId) {

        var entity = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            updateAt = null;
        }

        var response = new ResponseUserDto(
                entity.getUserId(),
                entity.getUsername(),
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRole().name(),
                entity.getCounterNumber(),
                entity.getActive(),
                entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                updateAt
        );

        this.userRepository.delete(entity);
        return response;
    }

    public ResponseUserStatisticsDto getStatistics() {

        return this.userRepository.getStatistics();
    }
}