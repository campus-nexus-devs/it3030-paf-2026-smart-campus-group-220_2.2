package com.smartcampus.it3030_paf_2026_smart_campus.controller;

import com.smartcampus.it3030_paf_2026_smart_campus.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public UserProfileResponse getProfile(@PathVariable Long userId) {
        return userProfileService.getProfile(userId);
    }

    @PutMapping("/{userId}")
    public UserProfileResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return userProfileService.updateProfile(userId, request);
    }
}
