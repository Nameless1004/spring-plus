package org.example.expert.domain.todo.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueryDateRangeSearch {

    private LocalDate startDate;
    private LocalDate endDate;
}
