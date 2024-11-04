package cn.lokn.knsharding.mybatis;

import cn.lokn.knsharding.demo.model.User;
import cn.lokn.knsharding.engine.ShardingContext;
import cn.lokn.knsharding.engine.ShardingEngine;
import cn.lokn.knsharding.engine.ShardingResult;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @description: Factory bean for mapper.
 * @author: lokn
 * @date: 2024/10/21 22:22
 */
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Setter
    ShardingEngine engine;

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
            Object[] params = getParams(boundSql, args);
            ShardingResult result = engine.shading(boundSql.getSql(), params);
            ShardingContext.set(result);

            return method.invoke(proxy, args);
        });
    }

    @SneakyThrows
    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            params = newParams;
        }
        return params;
    }

    private static Object getFieldValue(Object arg, String f) throws NoSuchFieldException, IllegalAccessException {
        Field field = arg.getClass().getDeclaredField(f);
        field.setAccessible(true);
        return field.get(arg);
    }
}
