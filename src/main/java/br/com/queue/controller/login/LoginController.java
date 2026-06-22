package br.com.queue.controller.login;

import br.com.queue.dtos.loginDto.RequestLoginDto;
import br.com.queue.dtos.tokenDto.RequestTokensDto;
import br.com.queue.dtos.tokenDto.ResponseTokens;
import br.com.queue.service.login.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ResponseTokens> login(@Valid @RequestBody RequestLoginDto request) {
        var tokens = this.loginService.login(request);
        return ResponseEntity.ok().body(tokens);
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<ResponseTokens> refreshTokens(@RequestBody RequestTokensDto request) {
        var tokens = this.loginService.refreshTokens(request);
        return ResponseEntity.ok().body(tokens);
    }
}
