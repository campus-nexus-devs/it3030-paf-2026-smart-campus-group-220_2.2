package com.smartcampus.it3030_paf_2026_smart_campus.controller;

import com.smartcampus.it3030_paf_2026_smart_campus.service.UserProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.smartcampus.it3030_paf_2026_smart_campus.dto.UserProfileDtos.UpdateUserProfileRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.UserProfileDtos.UserProfileResponse;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getProfile(@PathVariable Long userId, Authentication authentication) {
        ensureOwnerOrAdmin(userId, authentication);
        return userProfileService.getProfile(userId);
    }

    @PutMapping("/{userId}")
    public UserProfileResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequest request,
            Authentication authentication
    ) {
        ensureOwnerOrAdmin(userId, authentication);
        return userProfileService.updateProfile(userId, request);
    }

    private void ensureOwnerOrAdmin(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        if (isAdmin) {
            return;
        }
        String principalName = authentication.getName();
        if (!userProfileService.isOwnedByEmail(userId, principalName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own profile.");
        }
    }
}
