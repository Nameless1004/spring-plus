package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuerySearch {

    private String keyword;
    private String nickname;
    private QueryDateRangeSearch createdAtRange;
}
