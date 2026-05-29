package br.com.queue.controller.user;

import br.com.queue.dtos.user.ResponseUserDto;
import br.com.queue.dtos.user.create.CreateUserDto;
import br.com.queue.dtos.user.update.UpdateUserDto;
import br.com.queue.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseUserDto> createUser(
            @RequestBody CreateUserDto dto
    ) {

        var response = this.userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ResponseUserDto> update(
            @RequestBody UpdateUserDto dto
    ) {

        var response = this.userService.updateUser(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseUserDto>> getAllUsers(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search
    ) {

        var response = this.userService.getAllUsers(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> getUserById(
            @PathVariable String userId
    ) {

        var response = this.userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> delete(@PathVariable String userId) {

        var response = this.userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}