package org.example.expert.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByTodoIdWithUser(Long todoId) {
        QComment comment = QComment.comment;
        QUser user = QUser.user;
        QTodo todo = QTodo.todo;

        return queryFactory.select(comment)
            .from(comment)
            .join(comment.user, user).fetchJoin()
            .join(comment.todo, todo).fetchJoin()
            .where(comment.todo.id.eq(todoId))
            .fetch();
    }
}
