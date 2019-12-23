package org.tangbean.springjdbcdemo;

import io.micrometer.core.instrument.step.StepCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
public class BatchFooDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void batchInsert() {
        final String sql1 = "INSERT INTO FOO(BAR) VALUES(?)";
        List<String> datas = Arrays.asList("b-100", "b-101");
        jdbcTemplate.batchUpdate(sql1, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, datas.get(i));
            }

            @Override
            public int getBatchSize() {
                return datas.size();
            }
        });

        final String sql2 = "INSERT INTO FOO (ID, BAR) VALUES (:id, :bar)";
        List<Foo> foos = new ArrayList<>();
        foos.add(Foo.builder().id(100L).bar("named-100L").build());
        foos.add(Foo.builder().id(101L).bar("named-101L").build());
        namedParameterJdbcTemplate.batchUpdate(sql2, SqlParameterSourceUtils.createBatch(foos));
    }
}
