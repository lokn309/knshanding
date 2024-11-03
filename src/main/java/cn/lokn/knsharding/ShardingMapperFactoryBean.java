package cn.lokn.knsharding;

import cn.lokn.knsharding.demo.User;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @description: Factory bean for mapper.
 * @author: lokn
 * @date: 2024/10/21 22:22
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean(){}

    public ShardingMapperFactoryBean(Class<T> mapperInterface){
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        Class<T> clazz = getMapperInterface();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {

            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement statement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            Object parameterObject = args[0];
            System.out.println(" ===> getObject sql statement: " + boundSql.getSql());
            if (parameterObject instanceof User user) {
                ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
            } else if (parameterObject instanceof Integer id) {
                ShardingContext.set(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
            }

            return method.invoke(proxy, args);
        });
    }
}
