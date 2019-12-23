package org.tangbean.errorcodedemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ErrorCodeDemoApplication.class)
@Slf4j
class ErrorCodeDemoApplicationTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void testThrowingCustomException() {
//        final String sql = "SELECT COUNT(*) FROM FOO";
//        log.info("records count: {}", jdbcTemplate.queryForObject(sql, Integer.class));

        final String sql = "INSERT INTO FOO (ID, BAR) VALUES (1, 'aaa')";
        jdbcTemplate.update(sql);
        assertThrows(CustomDuplicatedKeyException.class, () -> jdbcTemplate.update(sql));
    }

}
