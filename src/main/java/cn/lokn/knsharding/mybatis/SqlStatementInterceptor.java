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
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
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
        ShardingResult result = ShardingContext.get();
        if (result != null) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            System.out.println(" ===> SqlStatementInterceptor: " + sql);
            String targetSqlStatement = result.getTargetSqlStatement();
            // 这里的if条件是优化，可以加，也可以不加
            if (!sql.equalsIgnoreCase(targetSqlStatement)) {
                replaceSql(boundSql, targetSqlStatement);
            }
        }
        return invocation.proceed();
    }

    private static void replaceSql(BoundSql boundSql, String sql) throws NoSuchFieldException {
        // 1、反射拿到sql的Field
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        // 直接修改内存
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, sql);
    }
}
