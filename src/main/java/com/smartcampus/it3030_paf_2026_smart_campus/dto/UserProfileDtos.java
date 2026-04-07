package com.smartcampus.it3030_paf_2026_smart_campus.dto;

public class UserProfileDtos {

    public record UserProfileResponse(
            Long userId,
            String firstName,
            String lastName,
            String fullName,
            String email,
            String contactNumber,
            String address,
            String profileImageData
    ) {
    }

    public record UpdateUserProfileRequest(
            String firstName,
            String lastName,
            String contactNumber,
            String address,
            String profileImageData
    ) {
    }

    /**
     * Summary row for admin user list (no password or large image payload).
     */
    public record UserAdminListResponse(
            Long userId,
            String fullName,
            String email,
            String contactNumber,
            String address,
            String registeredAt,
            String role
    ) {
    }
}
