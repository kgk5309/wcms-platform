package com.wcms.user.api;

import com.wcms.core.common.ApiResponse;
import com.wcms.user.application.UserProfileService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
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
}
