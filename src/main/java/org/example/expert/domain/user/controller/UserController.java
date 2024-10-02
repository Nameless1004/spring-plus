package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.security.UserDetailsImpl;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal UserDetailsImpl authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/profile")
    public void uploadProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestPart("profile") MultipartFile file) {
        userService.uploadProfileImage(userDetails.getUser(), file);
    }

    @PatchMapping("/users/profile")
    public void updateProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestPart("profile") MultipartFile file) {
        userService.uploadProfileImage(userDetails.getUser(), file);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<String> getProfileImageUrl(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getUserProfileImage(userDetails.getUser()));
    }

    @DeleteMapping("/users/profile")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteProfileImage(userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
