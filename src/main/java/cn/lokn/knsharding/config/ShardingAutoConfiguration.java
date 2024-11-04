package cn.lokn.knsharding.config;

import cn.lokn.knsharding.datasource.ShardingDataSource;
import cn.lokn.knsharding.engine.ShardingEngine;
import cn.lokn.knsharding.engine.StandardShardingEngine;
import cn.lokn.knsharding.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: Sharding auto configuration
 * @author: lokn
 * @date: 2024/10/21 21:37
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties properties) {
        return new StandardShardingEngine(properties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }

}
