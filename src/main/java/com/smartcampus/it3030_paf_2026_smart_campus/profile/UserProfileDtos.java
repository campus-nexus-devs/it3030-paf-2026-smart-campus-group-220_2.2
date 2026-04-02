package com.smartcampus.it3030_paf_2026_smart_campus.profile;

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
}
