package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.QueryDateRangeSearch;
import org.example.expert.domain.todo.dto.request.QuerySearch;
import org.example.expert.domain.todo.dto.request.QueryWeatherSearch;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {

    Page<TodoResponse> findAllTodoByQueryDsl(Pageable pageable, QueryWeatherSearch search, QueryDateRangeSearch dateRangeSearch);
    Page<TodoSearchResponse> search(Pageable pageable, QuerySearch querySearch);
}
