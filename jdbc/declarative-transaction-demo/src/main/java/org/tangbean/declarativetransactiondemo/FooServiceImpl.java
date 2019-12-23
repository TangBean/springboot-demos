package org.tangbean.declarativetransactiondemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@EnableAspectJAutoProxy(exposeProxy = true)
public class FooServiceImpl implements FooService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
//    @Autowired
//    private FooService fooService;

    @Override
    @Transactional
    public void insertRecord() {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('AAA')");
    }

    @Override
    @Transactional(rollbackFor = RollbackException.class)
    public void insertThenRollback() throws RollbackException {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('BBB')");
        throw new RollbackException();
    }

    @Override
//    @Transactional(rollbackFor = RollbackException.class)  // 再加一层事务法
    public void invokeInsertThenRollback() throws RollbackException {
//        insertThenRollback();

//        // 注入自己法
//        fooService.insertThenRollback();

        // 获取当前代理法
        ((FooService) AopContext.currentProxy()).insertThenRollback();
    }
}
