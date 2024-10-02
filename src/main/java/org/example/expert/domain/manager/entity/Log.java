package org.example.expert.domain.manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestUserId;
    private String requestUserNickname;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Log(Long requestUserId, String requestUserNickname, LocalDateTime createdAt) {
        this.requestUserId = requestUserId;
        this.requestUserNickname = requestUserNickname;
        this.createdAt = createdAt;
    }
}
