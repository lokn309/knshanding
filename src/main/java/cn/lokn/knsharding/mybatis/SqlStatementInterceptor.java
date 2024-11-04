package cn.lokn.knsharding.mybatis;

import cn.lokn.knsharding.demo.model.User;
import cn.lokn.knsharding.engine.ShardingContext;
import cn.lokn.knsharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @description: interceptor sql.
 * @author: lokn
 * @date: 2024/10/21 21:58
 */
@Intercepts(@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
    )
)
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        System.out.println(" ===> SqlStatementInterceptor: " + boundSql.getSql());
        Object parameterObject = boundSql.getParameterObject();
        System.out.println(" ===> SqlStatementInterceptor: " + boundSql.getParameterObject());
        // todo 修改sql， user -> user1
        return invocation.proceed();
    }
}
