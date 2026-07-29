package br.com.queue.service.user;

import br.com.queue.dtos.user.ResponseUserDto;
import br.com.queue.dtos.user.create.CreateUserDto;
import br.com.queue.dtos.user.get_user.ResponseUserInfoDto;
import br.com.queue.dtos.user.metrics.ResponseUserDashBoardDto;
import br.com.queue.dtos.user.update.UpdateUserDto;
import br.com.queue.dtos.user.users.ResponseAllUsersDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import br.com.queue.entities.unit.Unit;
import br.com.queue.entities.user.User;
import br.com.queue.enums.Role;
import br.com.queue.infra.UserNotFoundException;
import br.com.queue.infra.UserValidationException;
import br.com.queue.repositories.serviceManagement.ServiceManagementRepository;
import br.com.queue.repositories.user.UserRepository;
import br.com.queue.service.unit.UnitContext;
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
    private final UnitContext unitContext;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================== CREATE ===========================================================

    @Transactional
    public ResponseUserDto createUser(JwtAuthenticationToken token,CreateUserDto dto) {

        // Verifico em que unidade o usuario está logado,
        // e passo ao usuario que for criado para o sistema
        var unit = this.unitContext.getCurrentUnit(token);

        this.validateCreateUser(dto);

        var entity = this.toEntity(dto, unit);
        this.userRepository.save(entity);

        return this.toResponse(entity);
    }

    public void validateCreateUser(CreateUserDto dto) {

        var verifyUsername = this.userRepository.existsByUsername(dto.username());
        var verifyEmail = this.userRepository.existsByEmail(dto.email());
        var verifyPhone = this.userRepository.existsByPhone(dto.phone());
        var verifyCounterNumber = this.userRepository.existsByCounterNumber(dto.counterNumber());

        if (verifyUsername) {
            throw new UserValidationException("Este usuário já está cadastrado.");
        }

        if (verifyEmail) {
            throw new UserValidationException("Este e-mail já está cadastrado.");
        }

        if (verifyPhone) {
            throw new UserValidationException("Este telefone já está cadastrado.");
        }

        if (verifyCounterNumber) {
            throw new UserValidationException("Já possuí um usuário alocado para este guichê.");
        }
    }

    public User toEntity(CreateUserDto dto, Unit unit) {
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
        entity.setUnit(unit);
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

    // ===============================================================================================================

    // ============================================== UPDATE =========================================================
    @Transactional
    public ResponseUserDto updateUser(UpdateUserDto dto) {

        var entity = this.findUser(dto.userId());

        this.validateUpdateUser(dto, entity);

        this.updateEntity(dto, entity);

        this.userRepository.save(entity);

        return this.toResponse(entity);
    }

    private void validateUpdateUser(UpdateUserDto dto, User entity) {

        if (dto.username() != null
                && !dto.username().isBlank()
                && !dto.username().equals(entity.getUsername())
                && this.userRepository.existsByUsername(dto.username())) {

            throw new UserValidationException("Este usuário já está cadastrado.");
        }

        if (dto.email() != null
                && !dto.email().isBlank()
                && !dto.email().equals(entity.getEmail())
                && this.userRepository.existsByEmail(dto.email())) {

            throw new UserValidationException("Este e-mail já está cadastrado.");
        }

        if (dto.phone() != null
                && !dto.phone().isBlank()
                && !dto.phone().equals(entity.getPhone())
                && this.userRepository.existsByPhone(dto.phone())) {

            throw new UserValidationException("Este telefone já está cadastrado.");
        }

        if (dto.counterNumber() != null
                && !dto.counterNumber().equals(entity.getCounterNumber())
                && this.userRepository.existsByCounterNumber(dto.counterNumber())) {

            throw new UserValidationException("Já possui um usuário alocado para este guichê.");
        }
    }

    private void updateEntity(UpdateUserDto dto, User entity) {

        if (dto.username() != null && !dto.username().isBlank()) {
            entity.setUsername(dto.username());
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            entity.setName(dto.name());
        }

        if (dto.surname() != null && !dto.surname().isBlank()) {
            entity.setSurname(dto.surname());
        }

        if (dto.phone() != null && !dto.phone().isBlank()) {
            entity.setPhone(dto.phone());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            entity.setEmail(dto.email());
        }

        if (dto.role() != null && !dto.role().isBlank()) {
            entity.setRole(Role.valueOf(dto.role()));

            if (!Role.ATTENDANT.name().equals(dto.role())) {
                entity.setCounterNumber(null);
            }
        }

        if (dto.counterNumber() != null) {
            entity.setCounterNumber(dto.counterNumber());
        }

        if (dto.active() != null) {
            entity.setActive(dto.active());
        }

        if (dto.serviceIds() != null) {
            Set<ServiceManagement> services = new HashSet<>(
                    this.serviceManagementRepository.findAllByServiceManagementIdIn(dto.serviceIds())
            );
            entity.setServices(services);
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.password()));
        }

        entity.setUpdatedAt(LocalDateTime.now());
    }

    // ==============================================================================================================

    // ================================================= DELETE =====================================================
    @Transactional
    public ResponseUserDto deleteUser(String userId) {

        var entity = this.findUser(userId);
        var response = this.toResponse(entity);
        this.userRepository.delete(entity);
        return response;
    }
    // ==============================================================================================================


    // ============================================== ALL USERS =====================================================
    public Page<ResponseAllUsersDto> getAllUsers(JwtAuthenticationToken token ,int page, int size, String search) {

        var unit = this.unitContext.getCurrentUnit(token);

        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim();

        return this.userRepository.findAllWithSearch(
                unit.getUnitId(),
                normalizedSearch,
                PageRequest.of(page, size));
    }
    // ==============================================================================================================

    // ========================================== USER INFO =========================================================
    public ResponseUserInfoDto getUserById(String userId) {

        var entity = this.findUser(userId);
        return this.toInfoResponse(entity);
    }
    // ==============================================================================================================

    // ====================================== GET USER BY TOKEN =====================================================
    public ResponseUserInfoDto getUserByToken(JwtAuthenticationToken token) {

        var entity = this.findUser(token.getName());
        return this.toInfoResponse(entity);
    }
    // ==============================================================================================================

    // ========================================== GET STATISTICS ====================================================
    public ResponseUserDashBoardDto getStatistics(JwtAuthenticationToken token) {

        var unit = this.unitContext.getCurrentUnit(token);

        var countTotalUsersStatistics = this.userRepository.countTotalUsersStatisticsDto(unit.getUnitId());
        var userPercentagesStatistics = this.userRepository.getUserPercentagesStatisticsDto(unit.getUnitId());
        var usersCreatedByMonthStatistics = this.userRepository.countUsersCreatedByMonth(unit.getUnitId());
        var countServicesByUsers = this.userRepository.countServicesByUserStatistics(unit.getUnitId());
        var countRoleByUsers = this.userRepository.countUsersByRoleStatistics(unit.getUnitId());

        return new ResponseUserDashBoardDto(
                countTotalUsersStatistics,
                userPercentagesStatistics,
                usersCreatedByMonthStatistics,
                countServicesByUsers,
                countRoleByUsers
        );
    }
    // ===============================================================================================================

    // Serviços auxiliares
    public ResponseUserDto toResponse(User entity) {

        var updateAt = entity.getUpdatedAt() != null
                ? entity.getUpdatedAt().format(DATE_FORMATTER)
                : null;

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
                entity.getCreatedAt().format(DATE_FORMATTER),
                updateAt
        );
    }

    public ResponseUserInfoDto toInfoResponse(User entity) {
        var updateAt = "";

        if (entity.getUpdatedAt() != null ) {
            updateAt = entity.getUpdatedAt().format(DATE_FORMATTER);
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
                entity.getCreatedAt().format(DATE_FORMATTER),
                updateAt,
                entity.getServices()
                        .stream()
                        .map(ServiceManagement::getName)
                        .collect(Collectors.toSet())
        );
    }

    private User findUser(String userId) {
        return this.userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Usuário não encontrado"));
    }
}