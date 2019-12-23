package org.tangbean.springjdbcdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class FooDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SimpleJdbcInsert simpleJdbcInsert;

    public void insertData() {
        // 普通的insert
        Arrays.asList("b", "c").forEach(bar -> jdbcTemplate.update("INSERT INTO FOO (BAR) VALUES ( ? )", bar));

        // 使用simpleJdbcInsert的insert，要记得注入SimpleJdbcInsert对象
        HashMap<String, String> map = new HashMap<>();
        map.put("BAR", "d");
        Number id = simpleJdbcInsert.executeAndReturnKey(map);
        log.info("ID of d: {}", id.longValue());
    }

    public void listData() {
        // queryForObject
        log.info("Count: {}", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM FOO", Long.class));

        // queryForList
        List<String> barList = jdbcTemplate.queryForList("SELECT BAR FROM FOO", String.class);
        barList.forEach(bar -> log.info("Bar: {}", bar));

        // query
        List<Foo> fooList = jdbcTemplate.query("SELECT * FROM FOO", new RowMapper<Foo>() {
            @Override
            public Foo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Foo.builder()
                        .id(rs.getLong(1))
                        .bar(rs.getString(2))
                        .build();
            }
        });
        fooList.forEach(foo -> log.info("Foo: {}", foo));
    }
}
