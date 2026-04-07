package com.smartcampus.it3030_paf_2026_smart_campus.controller;

import com.smartcampus.it3030_paf_2026_smart_campus.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.smartcampus.it3030_paf_2026_smart_campus.dto.UserProfileDtos.UpdateUserProfileRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.UserProfileDtos.UserAdminListResponse;
import static com.smartcampus.it3030_paf_2026_smart_campus.dto.UserProfileDtos.UserProfileResponse;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserProfileService userProfileService;

    public AdminUserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserAdminListResponse> listUsers() {
        return userProfileService.listAllForAdmin();
    }

    @PutMapping("/{userId}")
    public UserProfileResponse updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return userProfileService.updateProfile(userId, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userProfileService.deleteUser(userId);
    }
}
