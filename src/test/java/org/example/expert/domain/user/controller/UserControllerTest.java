package org.example.expert.domain.user.controller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Test
    public void createMockData() throws Exception {

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 3_00_000; i++) {
            User user = new User(UUID.randomUUID().toString(), "123", UserRole.USER);
            ReflectionTestUtils.setField(user, "nickname", UUID.randomUUID().toString().replaceAll("-", ""));
            users.add(user);
        }

        insert(users);
    }

    public void insert(List<User> users) throws Exception {
        String sql = "INSERT INTO users (email, nickname, password) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getPassword());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
    }


    @Test
    public void findTest() throws Exception {
        // given
        Random rand = new Random();

        int count = 100;
        long total = 0;
        for(int i = 0; i < count; i++) {
            int random = rand.nextInt(1, 1_000_001);
            User user = userRepository.findById((long) random).get();
            String nickname = user.getNickname();


            long start = System.currentTimeMillis();
            Optional<User> byNickname = userRepository.findByNickname(nickname);
            long end = System.currentTimeMillis();

            long totalMs = end - start;
            total+=totalMs;
        }

        double average = (double)total / count;
        System.out.println("평균 소요 시간: " + average +"ms");
    }

}