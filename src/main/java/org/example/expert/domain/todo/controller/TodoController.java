package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.security.UserDetailsImpl;
import org.example.expert.domain.todo.dto.request.QueryDateRangeSearch;
import org.example.expert.domain.todo.dto.request.QuerySearch;
import org.example.expert.domain.todo.dto.request.QueryWeatherSearch;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser.getUser(), todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, new QueryWeatherSearch(weather), new QueryDateRangeSearch(startDate, endDate)));
    }

    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSearchResponse>> getTodos(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(todoService.searchTodos(page, size, new QuerySearch(keyword, nickname, new QueryDateRangeSearch(startDate, endDate))));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }
}
