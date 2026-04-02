package com.smartcampus.it3030_paf_2026_smart_campus.profile;

import com.smartcampus.it3030_paf_2026_smart_campus.auth.AppUser;
import com.smartcampus.it3030_paf_2026_smart_campus.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.smartcampus.it3030_paf_2026_smart_campus.profile.UserProfileDtos.UpdateUserProfileRequest;
import static com.smartcampus.it3030_paf_2026_smart_campus.profile.UserProfileDtos.UserAdminListResponse;
import static com.smartcampus.it3030_paf_2026_smart_campus.profile.UserProfileDtos.UserProfileResponse;

@Service
public class UserProfileService {

    public static final long PORTAL_ADMIN_USER_ID = 0L;

    private final AppUserRepository appUserRepository;
    private final String portalAdminEmail;
    private final String portalAdminFullName;

    public UserProfileService(
            AppUserRepository appUserRepository,
            @Value("${app.portal-admin.email:admin@gmail.com}") String portalAdminEmail,
            @Value("${app.portal-admin.full-name:System Admin}") String portalAdminFullName
    ) {
        this.appUserRepository = appUserRepository;
        this.portalAdminEmail = portalAdminEmail != null ? portalAdminEmail.trim() : "admin@gmail.com";
        this.portalAdminFullName = portalAdminFullName != null && !portalAdminFullName.isBlank()
                ? portalAdminFullName.trim()
                : "System Admin";
    }

    public UserProfileResponse getProfile(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return toResponse(user);
    }

    public List<UserAdminListResponse> listAllForAdmin() {
        String portalEmailNorm = portalAdminEmail.toLowerCase();
        UserAdminListResponse portalRow = new UserAdminListResponse(
                PORTAL_ADMIN_USER_ID,
                portalAdminFullName,
                portalAdminEmail,
                null,
                null,
                "",
                "ADMIN"
        );
        List<UserAdminListResponse> dbRows = appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(u -> {
                    String em = u.getEmail();
                    if (em == null) {
                        return true;
                    }
                    return !em.trim().toLowerCase().equals(portalEmailNorm);
                })
                .map(UserProfileService::toAdminListResponse)
                .toList();
        List<UserAdminListResponse> combined = new ArrayList<>(dbRows.size() + 1);
        combined.add(portalRow);
        combined.addAll(dbRows);
        return combined;
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (userId != null && userId == PORTAL_ADMIN_USER_ID) {
            throw new IllegalArgumentException("The portal administrator account cannot be deleted.");
        }
        if (!appUserRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        appUserRepository.deleteById(userId);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateUserProfileRequest request) {
        if (userId != null && userId == PORTAL_ADMIN_USER_ID) {
            throw new IllegalArgumentException(
                    "The portal administrator is not stored in the database and cannot be updated here."
            );
        }
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

    private static UserAdminListResponse toAdminListResponse(AppUser u) {
        String registered = u.getCreatedAt() != null ? u.getCreatedAt().toString() : "";
        return new UserAdminListResponse(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getContactNumber(),
                u.getAddress(),
                registered,
                "USER"
        );
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
