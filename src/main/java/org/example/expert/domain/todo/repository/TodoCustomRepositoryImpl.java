package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.QueryDateRangeSearch;
import org.example.expert.domain.todo.dto.request.QuerySearch;
import org.example.expert.domain.todo.dto.request.QueryWeatherSearch;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
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
        whereBB.and(byWeather(search)).and(byModifiedDate(dateRangeSearch));

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

    public Page<TodoSearchResponse> search(Pageable pageable, QuerySearch querySearch) {
        QTodo todo = QTodo.todo;
        QUser todoUser = new QUser("todoUser");
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        BooleanBuilder search = new BooleanBuilder();
        search.and(containsKeyword(querySearch.getKeyword()).and(byCreatedDate(querySearch.getCreatedAtRange()).and(byManagerNickname(querySearch.getNickname()))));

        List<TodoSearchResponse> content = queryFactory
            .select(Projections.constructor(TodoSearchResponse.class,
                todo.title,
                JPAExpressions.select(manager.count())
                    .from(manager)
                    .where(manager.todo.eq(todo)),
                JPAExpressions.select(comment.count())
                    .from(comment)
                    .where(comment.todo.eq(todo)),
                todo.createdAt
            ))
            .distinct()
            .from(todo)
            .leftJoin(todo.user, todoUser)
            .leftJoin(todo.managers, manager)
            .where(search)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        Long count = queryFactory.select(Wildcard.count)
            .distinct()
            .from(todo)
            .leftJoin(todo.managers, manager)
            .where(search)
            .fetchOne();

        System.out.println("count = " + count);

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    public BooleanBuilder byWeather(QueryWeatherSearch search) {
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(search.getWeather())) {
            builder.and(QTodo.todo.weather.eq(search.getWeather()));
        }

        return builder;
    }

    public BooleanBuilder containsKeyword(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(keyword)) {
            builder.and(QTodo.todo.title.containsIgnoreCase(keyword));
        }

        return builder;
    }

    public BooleanBuilder byManagerNickname(String nickname) {
        BooleanBuilder builder = new BooleanBuilder();
        if(StringUtils.hasText(nickname)) {
            builder.and(QManager.manager.user.nickname.containsIgnoreCase(nickname));
        }
        return builder;
    }

    public BooleanBuilder byModifiedDate(QueryDateRangeSearch search) {
        BooleanBuilder builder = new BooleanBuilder();

        if(search.getStartDate() != null) {
            builder.and(QTodo.todo.modifiedAt.after(search.getStartDate().atStartOfDay()));
        }

        if(search.getEndDate() != null) {
            builder.and(QTodo.todo.modifiedAt.before(search.getEndDate().atTime(23, 59, 59, 999_999_999)));
        }

        return builder;
    }

    public BooleanBuilder byCreatedDate(QueryDateRangeSearch search) {
        BooleanBuilder builder = new BooleanBuilder();

        if(search.getStartDate() != null) {
            builder.and(QTodo.todo.createdAt.after(search.getStartDate().atStartOfDay()));
        }

        if(search.getEndDate() != null) {
            builder.and(QTodo.todo.createdAt.before(search.getEndDate().atTime(23, 59, 59, 999_999_999)));
        }

        return builder;
    }

}
