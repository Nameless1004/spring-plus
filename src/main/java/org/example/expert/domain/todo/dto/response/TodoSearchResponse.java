package org.example.expert.domain.todo.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSearchResponse {
    private String title;
    private Long managerCount;
    private Long totalCommentCount;
    private LocalDateTime createdAt;
}
