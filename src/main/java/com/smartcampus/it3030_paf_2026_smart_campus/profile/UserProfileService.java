package com.smartcampus.it3030_paf_2026_smart_campus.profile;

import com.smartcampus.it3030_paf_2026_smart_campus.auth.AppUser;
import com.smartcampus.it3030_paf_2026_smart_campus.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smartcampus.it3030_paf_2026_smart_campus.profile.UserProfileDtos.UpdateUserProfileRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.profile.UserProfileDtos.UserProfileResponse;

@Service
public class UserProfileService {

    private final AppUserRepository appUserRepository;

    public UserProfileService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public UserProfileResponse getProfile(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateUserProfileRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String fn = request.firstName() != null ? request.firstName().trim() : "";
        String ln = request.lastName() != null ? request.lastName().trim() : "";
        user.setFirstName(fn);
        user.setLastName(ln);
        String combined = (fn + " " + ln).trim();
        if (!combined.isEmpty()) {
            user.setFullName(combined);
        }

        if (request.contactNumber() != null) {
            String c = request.contactNumber().trim();
            user.setContactNumber(c.isEmpty() ? null : c);
        }
        if (request.address() != null) {
            String a = request.address().trim();
            user.setAddress(a.isEmpty() ? null : a);
        }
        if (request.profileImageData() != null) {
            applyProfileImage(user, request.profileImageData().trim());
        }

        return toResponse(appUserRepository.save(user));
    }

    private static void applyProfileImage(AppUser user, String img) {
        if (img.isEmpty() || img.contains("via.placeholder.com")) {
            user.setProfileImageData(null);
            return;
        }
        if (img.startsWith("data:image")) {
            user.setProfileImageData(img);
        }
    }

    private static UserProfileResponse toResponse(AppUser u) {
        String first = u.getFirstName();
        String last = u.getLastName();
        if ((first == null || first.isBlank()) && (last == null || last.isBlank())) {
            String full = u.getFullName() != null ? u.getFullName() : "";
            String[] parts = full.trim().split("\\s+", 2);
            first = parts.length > 0 ? parts[0] : "";
            last = parts.length > 1 ? parts[1] : "";
        }
        return new UserProfileResponse(
                u.getId(),
                first != null ? first : "",
                last != null ? last : "",
                u.getFullName(),
                u.getEmail(),
                u.getContactNumber(),
                u.getAddress(),
                u.getProfileImageData()
        );
    }
}
