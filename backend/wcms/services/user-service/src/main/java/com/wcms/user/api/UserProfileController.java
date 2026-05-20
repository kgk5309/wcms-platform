package com.wcms.user.api;

import com.wcms.core.common.ApiResponse;
import com.wcms.user.application.UserOnboardingService;
import com.wcms.user.application.UserProfileService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/profiles")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserOnboardingService userOnboardingService;

    public UserProfileController(UserProfileService userProfileService, UserOnboardingService userOnboardingService) {
        this.userProfileService = userProfileService;
        this.userOnboardingService = userOnboardingService;
    }

    @PostMapping("/onboarding")
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OnboardUserResponse> onboard(
            @Valid @RequestBody OnboardUserRequest request,
            @org.springframework.web.bind.annotation.RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ApiResponse.success(OnboardUserResponse.from(userOnboardingService.onboard(
                request.toCommand(),
                authorization
        )));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserProfileResponse> create(@Valid @RequestBody CreateUserProfileRequest request) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.create(request.toCommand())));
    }

    @GetMapping("/{id}")
    ApiResponse<UserProfileResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.get(id)));
    }

    @GetMapping
    ApiResponse<List<UserProfileResponse>> findAll() {
        return ApiResponse.success(userProfileService.findAll().stream()
                .map(UserProfileResponse::from)
                .toList());
    }

    @PatchMapping("/{id}")
    ApiResponse<UserProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.updateProfile(id, request.toCommand())));
    }

    @PatchMapping("/{id}/role")
    ApiResponse<UserProfileResponse> changeRole(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeUserRoleRequest request
    ) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.changeRole(id, request.toCommand())));
    }

    @PatchMapping("/{id}/scope")
    ApiResponse<UserProfileResponse> moveScope(
            @PathVariable UUID id,
            @Valid @RequestBody MoveUserScopeRequest request
    ) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.moveScope(id, request.toCommand())));
    }

    @PostMapping("/{id}/disable")
    ApiResponse<UserProfileResponse> disable(@PathVariable UUID id) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.disable(id)));
    }

    @PostMapping("/{id}/activate")
    ApiResponse<UserProfileResponse> activate(@PathVariable UUID id) {
        return ApiResponse.success(UserProfileResponse.from(userProfileService.activate(id)));
    }
}
