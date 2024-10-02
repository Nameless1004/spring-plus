package org.example.expert.domain.manager.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLog(User requestUser) {
        Log log = Log.builder()
            .requestUserId(requestUser.getId())
            .requestUserNickname(requestUser.getNickname())
            .createdAt(LocalDateTime.now())
            .build();

        logRepository.save(log);
    }

}
