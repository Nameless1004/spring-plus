package org.example.expert.domain.user.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.cloudfront.domain}")
    private String cloudFrontDomain;

    private final S3Client s3Client;

    @Transactional(readOnly = false)
    public void uploadProfileImage(User user, MultipartFile file) {

        String uploadDir = System.getProperty("java.io.tmpdir");  // 임시 디렉토리
        String savePath = uploadDir + File.separator + "user_" + user.getId() + "_" + "profile_" + file.getOriginalFilename();
        String key =  "user/" + user.getId() + "/" + "profile/" + UUID.randomUUID().toString().substring(0, 7) + file.getOriginalFilename();

        File f = new File(savePath);

        if(StringUtils.hasText(user.getProfileImageKey())) {
            deleteS3(user.getProfileImageKey());
        }

        try {
            file.transferTo(f);
            PutObjectRequest build = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            RequestBody requestBody = RequestBody.fromFile(f);
            s3Client.putObject(build, requestBody);
            String url = cloudFrontDomain + "/" + key;
            user.updateProfileImageKey(key);
            user.updateProfileImageUrl(url);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            f.delete();
        }
    }

    private void deleteS3(String key){
        DeleteObjectRequest req = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        s3Client.deleteObject(req);
    }

    public void deleteProfileImage(User user) {
        if(StringUtils.hasText(user.getProfileImageKey())) {
            deleteS3(user.getProfileImageKey());
            user.updateProfileImageKey(null);
            user.updateProfileImageUrl(null);
        }
    }

    public String getUserProfileImage(User user) {
        return user.getProfileImageUrl();
    }
}
