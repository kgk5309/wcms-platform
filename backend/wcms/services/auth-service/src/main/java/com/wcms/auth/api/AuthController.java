package com.wcms.auth.api;

import com.wcms.core.common.ApiResponse;
import com.wcms.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthAccountResponse> createAccount(@Valid @RequestBody CreateAuthAccountRequest request) {
        return ApiResponse.success(AuthAccountResponse.from(authService.createAccount(request.toCommand())));
    }

    @PostMapping("/accounts/{id}/disable")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<AuthAccountResponse> disableAccount(@PathVariable UUID id) {
        return ApiResponse.success(AuthAccountResponse.from(authService.disableAccount(id)));
    }

    @PostMapping("/accounts/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<AuthAccountResponse> activateAccount(@PathVariable UUID id) {
        return ApiResponse.success(AuthAccountResponse.from(authService.activateAccount(id)));
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return authService.login(request, requestMeta(servletRequest));
    }

    @PostMapping("/refresh")
    public AuthTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest servletRequest) {
        return authService.refresh(request, requestMeta(servletRequest));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }

    private RequestMeta requestMeta(HttpServletRequest request) {
        return new RequestMeta(request.getRemoteAddr(), request.getHeader("User-Agent"));
    }
}
