package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.QueryDateRangeSearch;
import org.example.expert.domain.todo.dto.request.QueryWeatherSearch;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoResponse> findAllTodoByQueryDsl(Pageable pageable, QueryWeatherSearch search, QueryDateRangeSearch dateRangeSearch) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        BooleanBuilder whereBB = new BooleanBuilder();
        whereBB.and(byWeather(search)).and(byDate(dateRangeSearch));

        List<TodoResponse> content = queryFactory
            .select(Projections.constructor(TodoResponse.class,
                todo.id,
                todo.title,
                todo.contents,
                todo.weather,
                Projections.constructor(UserResponse.class,
                    user.id,
                    user.email
                ),
                todo.createdAt,
                todo.modifiedAt
                ))
            .distinct()
            .from(todo)
            .leftJoin(todo.user, user)
            .where(whereBB)
            .orderBy(todo.modifiedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        Long count = queryFactory.select(Wildcard.count)
            .from(todo)
            .where(whereBB)
            .fetchFirst();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    public BooleanBuilder byWeather(QueryWeatherSearch search) {
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(search.getWeather())) {
            builder.and(QTodo.todo.weather.eq(search.getWeather()));
        }

        return builder;
    }

    public BooleanBuilder byDate(QueryDateRangeSearch search) {
        BooleanBuilder builder = new BooleanBuilder();

        if(search.getStartDate() != null) {
            builder.and(QTodo.todo.modifiedAt.after(search.getStartDate().atStartOfDay()));
        }

        if(search.getEndDate() != null) {
            builder.and(QTodo.todo.modifiedAt.before(search.getEndDate().atTime(23, 59, 59, 999_999_999)));
        }

        return builder;
    }
}
